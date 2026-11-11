'use strict';
(() => {
  const form=document.querySelector('#auth-form'), error=document.querySelector('#auth-error');
  let registering=false;
  const token=async()=>await (await fetch('/api/auth/csrf')).json();
  document.querySelector('#auth-switch').addEventListener('click',()=>{
    registering=!registering; error.textContent='';
    document.querySelector('#auth-title').textContent=registering?'경험을 모을 준비가 됐나요?':'다시 만나서 반가워요';
    document.querySelector('#auth-subtitle').textContent=registering?'이메일로 나만의 공간을 만들어보세요.':'로그인하고 나의 경험을 이어서 기록해요.';
    document.querySelector('#auth-submit').textContent=registering?'회원가입':'로그인';
    document.querySelector('#auth-switch-label').textContent=registering?'이미 계정이 있나요?':'아직 계정이 없나요?';
    document.querySelector('#auth-switch').textContent=registering?'로그인':'회원가입';
    form.elements.password.autocomplete=registering?'new-password':'current-password';
  });
  form.addEventListener('submit',async e=>{
    e.preventDefault(); error.textContent=''; const button=document.querySelector('#auth-submit');button.disabled=true;
    try {
      let csrf=await token(); const email=form.elements.email.value.trim().toLowerCase(), password=form.elements.password.value;
      if(registering) {
        const response=await fetch('/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json',[csrf.headerName]:csrf.token},body:JSON.stringify({email,password})});
        if(!response.ok) {const body=await response.json().catch(()=>({}));throw new Error(body.message||'가입하지 못했어요. 입력 내용을 확인해주세요.');}
      }
      csrf=await token();
      const response=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded',[csrf.headerName]:csrf.token},body:new URLSearchParams({username:email,password})});
      if(!response.ok) throw new Error('이메일 또는 비밀번호를 확인해주세요.');
      location.href='/';
    } catch(err) {error.textContent=err.message||'서버에 연결하지 못했어요.';} finally {button.disabled=false;}
  });
})();
