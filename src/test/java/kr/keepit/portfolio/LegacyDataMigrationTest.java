package kr.keepit.portfolio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

class LegacyDataMigrationTest {
    @TempDir Path dir;
    @Test void refusesToCopyAnInUseDatabase() throws Exception {
        Path old = dir.resolve("moa.mv.db"); Files.writeString(old, "data");
        try (FileChannel channel = FileChannel.open(old, StandardOpenOption.WRITE); var lock = channel.lock()) {
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> LegacyDataMigration.copyIfNeeded(dir)).isInstanceOf(IOException.class);
        }
        assertThat(Files.exists(dir.resolve("keepit.mv.db"))).isFalse();
    }
    @Test void copiesOldDataOnlyWhenNewDatabaseDoesNotExist() throws Exception {
        Files.writeString(dir.resolve("moa.mv.db"), "old data");
        LegacyDataMigration.copyIfNeeded(dir);
        assertThat(Files.readString(dir.resolve("keepit.mv.db"))).isEqualTo("old data");
        Files.writeString(dir.resolve("keepit.mv.db"), "new data");
        LegacyDataMigration.copyIfNeeded(dir);
        assertThat(Files.readString(dir.resolve("keepit.mv.db"))).isEqualTo("new data");
        assertThat(Files.readString(dir.resolve("moa.mv.db"))).isEqualTo("old data");
    }
}
