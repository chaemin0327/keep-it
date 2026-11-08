package kr.keepit.portfolio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.*;

/** Copies the old H2 database once, keeping the original as a backup. */
final class LegacyDataMigration {
    private LegacyDataMigration() {}

    static void copyIfNeeded(Path directory) throws IOException {
        Path oldFile = directory.resolve("moa.mv.db"), newFile = directory.resolve("keepit.mv.db");
        if (!Files.exists(oldFile) || Files.exists(newFile)) return;
        Path temp = Files.createTempFile(directory, "keepit-migration-", ".tmp");
        try (FileChannel channel = FileChannel.open(oldFile, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            try (var lock = channel.tryLock()) {
                if (lock == null) throw new IOException("기존 데이터가 사용 중입니다. 실행 중인 킵잇을 종료하고 다시 시도하세요.");
                Files.copy(oldFile, temp, StandardCopyOption.REPLACE_EXISTING);
                try { Files.move(temp, newFile); }
                catch (FileAlreadyExistsException ignored) { /* Another instance completed migration. */ }
            } catch (OverlappingFileLockException e) {
                throw new IOException("기존 데이터가 사용 중입니다. 실행 중인 킵잇을 종료하고 다시 시도하세요.", e);
            }
        } finally { Files.deleteIfExists(temp); }
    }
}
