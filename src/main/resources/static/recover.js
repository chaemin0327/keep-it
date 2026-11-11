'use strict';
(() => {
  const $=s=>document.querySelector(s), form=$('#recovery-form'), message=$('#recovery-message');
  const parts=location.hash.slice(1).split('/'), mode=parts[0], token=parts[1];
  // Do not leave tokens in navigation history once the page is open.
  if(token) history.replaceState(null,'',location.pathname);
  let resend=false;
  if(mode==='reset' && token) {
    $('#recovery-title').textContent='새 비밀번호 설정'; $('#recovery-description').textContent='새 비밀번호를 입력해주세요.';
    $('#email-label').hidden=true; $('#password-label').hidden=false; $('#recovery-password').required=true;
    $('#recovery-submit').textContent='비밀번호 변경'; $('#resend-row').hidden=true;
  } else if(mode==='verify' && token) {
    $('#recovery-title').textContent='이메일 인증'; $('#recovery-description').textContent='버튼을 눌러 이메일 인증을 완료해주세요.';
    $('#email-label').hidden=true; $('#recovery-submit').textContent='인증 완료하기'; $('#resend-row').hidden=true;
  } else { $('#recovery-email').required=true; }
  $('#resend').addEventListener('click',()=>{resend=!resend;$('#recovery-submit').textContent=resend?'인증 메일 보내기':'비밀번호 재설정 메일 보내기';message.textContent='';});
  form.addEventListener('submit',async e=>{
    e.preventDefault();const button=$('#recovery-submit');button.disabled=true;message.textContent='';
    try {
      const csrf=await (await fetch('/api/auth/csrf')).json();
      const path=mode==='verify'&&token?'verify':mode==='reset'&&token?'password/reset':resend?'verify/resend':'password/forgot';
      const body=mode==='verify'&&token?{token}:mode==='reset'&&token?{token,password:$('#recovery-password').value}:{email:$('#recovery-email').value.trim()};
      const response=await fetch('/api/auth/'+path,{method:'POST',headers:{'Content-Type':'application/json',[csrf.headerName]:csrf.token},body:JSON.stringify(body)});
      if(!response.ok) {const err=await response.json().catch(()=>({}));throw new Error(err.message||'요청을 처리하지 못했어요.');}
      message.textContent=token?(mode==='verify'?'인증이 완료됐어요. 로그인해주세요.':'비밀번호를 변경했어요. 로그인해주세요.'):'해당하는 계정이 있다면 이메일로 안내를 보냈어요.';
      if(token) button.hidden=true;
    } catch(err) {message.textContent=err.message||'서버에 연결하지 못했어요.';} finally {button.disabled=false;}
  });
})();
