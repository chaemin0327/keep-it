'use strict';
(() => {
  const paths = {
    archive: '<rect x="3" y="4" width="18" height="4" rx="1"/><path d="M5 8v12h14V8M9 12h6"/>',
    timeline: '<path d="M6 3v18M10 6h10M10 12h7M10 18h10"/><circle cx="6" cy="6" r="2"/><circle cx="6" cy="12" r="2"/><circle cx="6" cy="18" r="2"/>',
    portfolio: '<rect x="3" y="7" width="18" height="14" rx="2"/><path d="M8 7V4h8v3M3 12h18M9 12v3h6v-3"/>',
    project: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M8 21h8M12 17v4M8 8l-2 2 2 2M16 8l2 2-2 2"/>',
    trophy: '<path d="M8 3h8v5a4 4 0 0 1-8 0V3ZM8 5H4v2a4 4 0 0 0 4 4m8-6h4v2a4 4 0 0 1-4 4M12 12v6m-4 3h8m-6-3h4v3"/>',
    certificate: '<rect x="4" y="3" width="16" height="15" rx="2"/><path d="M8 7h8M8 10h4M10 17v5l3-2 3 2v-5"/><circle cx="13" cy="15" r="3"/>',
    paper: '<path d="M14 2H5v20h14V7l-5-5ZM14 2v6h5M8 12h8M8 16h8M8 19h4"/>',
    globe: '<circle cx="12" cy="12" r="9"/><ellipse cx="12" cy="12" rx="4" ry="9"/><path d="M3 12h18M5 7h14M5 17h14"/>',
    people: '<circle cx="9" cy="8" r="3"/><path d="M3 21v-3a6 6 0 0 1 12 0v3M16 5a3 3 0 0 1 0 6m2 10v-3a6 6 0 0 0-2-4"/>',
    book: '<path d="M12 5C9 3 5 3 3 4v15c3-1 6-1 9 1 3-2 6-2 9-1V4c-3-1-6-1-9 1ZM12 5v15"/>',
    star: '<path d="m12 3 2.8 5.6 6.2.9-4.5 4.4 1.1 6.2-5.6-3-5.6 3 1.1-6.2L3 9.5l6.2-.9L12 3Z"/>',
    calendar: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 11h18"/>',
    plus: '<path d="M12 5v14M5 12h14"/>',
    grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
    list: '<path d="M9 5h12M9 12h12M9 19h12M3 5h1M3 12h1M3 19h1"/>',
    search: '<circle cx="10" cy="10" r="7"/><path d="m15 15 6 6"/>',
    settings: '<path d="M4 7h16M4 17h16"/><circle cx="9" cy="7" r="3" fill="currentColor" stroke="none"/><circle cx="15" cy="17" r="3" fill="currentColor" stroke="none"/>',
    chevronDown: '<path d="m6 9 6 6 6-6"/>',
    arrowRight: '<path d="M4 12h16m-6-6 6 6-6 6"/>',
    close: '<path d="m6 6 12 12M6 18 18 6"/>',
    info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7h.01"/>',
    download: '<path d="M12 3v12m-5-5 5 5 5-5M4 16v5h16v-5"/>',
    edit: '<path d="m15 4 5 5M3 21l5-1L21 7l-5-5L3 15v6Z"/>',
    trash: '<path d="M3 6h18M9 6V3h6v3M5 6l1 15h12l1-15M10 10v7M14 10v7"/>',
    menu: '<path d="M3 5h18M3 12h18M3 19h18"/>',
    sprout: '<path d="M12 21v-9M12 14C5 14 3 10 3 4c6 0 9 3 9 10Zm0-3c0-5 3-8 9-8 0 6-3 9-9 9"/>'
  };
  const icon = name => `<svg class="icon" viewBox="0 0 24 24" aria-hidden="true">${paths[name] || paths.archive}</svg>`;
  document.querySelectorAll('[data-icon]').forEach(el => el.innerHTML = icon(el.dataset.icon));
  const types = {
    WORK: {label:'인턴/경력', icon:'portfolio', color:'#475569', bg:'#f1f5f9'},
    PROJECT: {label:'프로젝트', icon:'project', color:'#475569', bg:'#f1f5f9'},
    COMPETITION: {label:'공모전', icon:'trophy', color:'#475569', bg:'#f1f5f9'},
    AWARD: {label:'수상 경력', icon:'trophy', color:'#475569', bg:'#f1f5f9'},
    CERTIFICATE: {label:'자격증', icon:'certificate', color:'#475569', bg:'#f1f5f9'},
    PAPER: {label:'논문', icon:'paper', color:'#475569', bg:'#f1f5f9'},
    OVERSEAS: {label:'해외 경험', icon:'globe', color:'#475569', bg:'#f1f5f9'},
    ACTIVITY: {label:'동아리/대외활동', icon:'people', color:'#475569', bg:'#f1f5f9'},
    EDUCATION: {label:'교육 / 기타', icon:'book', color:'#475569', bg:'#f1f5f9'},
    CUSTOM: {label:'직접 추가', icon:'plus', color:'#475569', bg:'#f1f5f9'}
  };
  const $ = selector => document.querySelector(selector);
  const escape = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const state = {experiences:[], profile:{name:'나의 이름'}, page:'archive', filter:'ALL', query:'', sort:'newest', layout:'grid', editing:null, loaded:false};
  const typeStyle = type => `--type-color:${types[type].color};--type-bg:${types[type].bg}`;
  const categoryLabel = e => e.type === 'CUSTOM' ? (e.customCategory || '직접 추가') : types[e.type].label;
  const categoryKey = e => e.type === 'CUSTOM' ? `custom:${e.customCategory}` : e.type;
  const matchesCategory = (e, key) => key === 'ALL' || categoryKey(e) === key;
  const customNames = () => [...new Set(state.experiences.filter(e => e.type === 'CUSTOM').map(e => e.customCategory).filter(Boolean))].sort((a,b) => a.localeCompare(b,'ko'));
  const categoryOptions = () => [...Object.entries(types).filter(([k]) => k !== 'CUSTOM').map(([key,v]) => ({key,label:v.label})), ...customNames().map(label => ({key:`custom:${label}`,label}))];
  const badge = e => `<span class="type-badge" style="${typeStyle(e.type)}">${icon(types[e.type].icon)}${escape(categoryLabel(e))}</span>`;
  const date = value => value ? value.slice(0,7).replace('-', '.') : '';
  const period = e => e.ongoing ? `${date(e.startDate)} — 진행 중` : e.endDate && e.endDate !== e.startDate ? `${date(e.startDate)} — ${date(e.endDate)}` : date(e.startDate);
  const tags = value => (value || '').split(',').map(s => s.trim()).filter(Boolean);
  const tagHtml = (value, limit=4) => tags(value).slice(0,limit).map(t => `<span class="tag">${escape(t)}</span>`).join('');
  function safeLink(value) {
    try { const u = new URL(value); return ['https:', 'http:'].includes(u.protocol) ? escape(u.href) : ''; } catch { return ''; }
  }
  const resultSummary = e => [e.placement,e.awardName].filter(Boolean).join(' · ');
  const linksFor = e => Array.isArray(e.links) ? e.links : e.link ? [e.link] : [];
  const linksHtml = e => `<div class="related-links">${linksFor(e).filter(safeLink).map(url => `<a href="${safeLink(url)}" target="_blank" rel="noopener noreferrer">${escape(url)} ↗</a>`).join('')}</div>`;
  const resultHtml = e => resultSummary(e) ? `<section class="detail-section outcome"><h3>최종 결과</h3><p>${escape(resultSummary(e))}</p></section>` : '';
  function updateLinkRows() {
    const rows = [...$('#experience-links').children];
    rows.forEach((row,index) => {
      row.querySelector('input').setAttribute('aria-label',`관련 링크 ${index+1}`);
      row.querySelector('button').setAttribute('aria-label',`${index+1}번 링크 삭제`);
    });
    $('#add-link').disabled = rows.length >= 10;
  }
  function addLinkRow(value='') {
    if ($('#experience-links').children.length >= 10) return;
    const row = document.createElement('div'); row.className = 'link-row';
    row.innerHTML = `<label class="field"><input name="links" type="url" maxlength="1000" placeholder="https://github.com/..." value="${escape(value)}"></label><button class="icon-button" type="button" data-remove-link title="링크 삭제">${icon('close')}</button>`;
    $('#experience-links').append(row); updateLinkRows();
  }
  function renderLinkInputs(values) {
    $('#experience-links').replaceChildren();
    (values.length ? values : ['']).forEach(addLinkRow);
  }
  async function api(path, options={}) {
    const response = await fetch(`/api${path}`, {headers:{'Content-Type':'application/json'}, ...options});
    if (!response.ok) {
      const error = await response.json().catch(() => ({}));
      throw new Error(error.message || '저장하지 못했어요. 앱 연결 상태를 확인해주세요.');
    }
    return response.status === 204 ? null : response.json();
  }
  let toastTimer;
  function toast(message, error=false) {
    clearTimeout(toastTimer); $('#toast').textContent = message;
    $('#toast').className = `toast visible${error ? ' error' : ''}`;
    toastTimer = setTimeout(() => $('#toast').classList.remove('visible'), 3600);
  }
  const run = action => Promise.resolve().then(action).catch(e => toast(e.message, true));
  const closeDialogs = () => document.querySelectorAll('dialog[open]').forEach(d => d.close());
  function showDialog(id) { closeDialogs(); $(id).showModal(); }
  function renderStats() {
    const es = state.experiences;
    const stats = [
      ['archive','전체 경험',es.length,'개','var(--accent-light)','var(--accent)'],
      ['timeline','진행 중',es.filter(e => e.ongoing).length,'개','var(--accent-light)','var(--accent)'],
      ['star','포트폴리오',es.filter(e => e.featured).length,'개','#fff7ed','#b45309']
    ];
    $('#stats').innerHTML = stats.map(([i,l,n,u,b,c]) => `<div class="stat" style="--stat-bg:${b};--stat-color:${c}"><span class="stat-icon">${icon(i)}</span><span class="stat-copy"><span class="stat-label">${l}</span><strong>${n}<small>${u}</small></strong></span></div>`).join('');
  }
  function renderFilters() {
    const options = categoryOptions();
    const count = key => state.experiences.filter(e => matchesCategory(e,key)).length;
    if (state.filter !== 'ALL' && !options.some(o => o.key === state.filter)) state.filter = 'ALL';
    $('#category-nav').innerHTML = options.map(({key,label}) => `<button class="category-item ${state.page === 'archive' && state.filter === key ? 'active' : ''}" data-filter="${escape(key)}"><span class="category-dot"></span><span class="category-name" title="${escape(label)}">${escape(label)}</span><span class="category-count">${count(key)}</span></button>`).join('');
    const used = options.filter(o => count(o.key) > 0 || o.key === state.filter);
    $('#filter-chips').innerHTML = [{key:'ALL',label:'전체'},...used].map(({key,label}) => `<button class="filter-chip ${state.filter === key ? 'active' : ''}" data-filter="${escape(key)}" aria-pressed="${state.filter === key}">${escape(label)}${key === 'ALL' ? `<span>${state.experiences.length}</span>` : ''}</button>`).join('');
    const selected = $('#timeline-type').value;
    $('#timeline-type').innerHTML = '<option value="ALL">모든 경험</option>' + options.filter(o => count(o.key) > 0).map(o => `<option value="${escape(o.key)}">${escape(o.label)}</option>`).join('');
    $('#timeline-type').value = options.some(o => o.key === selected && count(o.key) > 0) ? selected : 'ALL';
    $('#custom-category-options').innerHTML = customNames().map(name => `<option value="${escape(name)}"></option>`).join('');
    $('#nav-count').textContent = state.experiences.length;
    $('#experience-count').textContent = state.experiences.length;
  }
  function filtered() {
    const q = state.query.toLocaleLowerCase();
    return state.experiences.filter(e => matchesCategory(e,state.filter) &&
      [e.title,e.organization,e.location,e.tags,e.description,e.participants,e.role,e.placement,e.awardName,categoryLabel(e)].some(s => (s || '').toLocaleLowerCase().includes(q)))
      .sort((a,b) => state.sort === 'title' ? a.title.localeCompare(b.title, 'ko') :
        state.sort === 'oldest' ? a.startDate.localeCompare(b.startDate) || a.id-b.id : b.startDate.localeCompare(a.startDate) || b.id-a.id);
  }
  function card(e) {
    const award = [ 'COMPETITION','AWARD' ].includes(e.type) && (resultSummary(e) || e.outcome);
    return `<article class="experience-card" data-id="${e.id}" style="${typeStyle(e.type)}"><div class="card-top">${badge(e)}<button class="star-button ${e.featured?'selected':''}" data-star="${e.id}" aria-label="${e.featured?'포트폴리오에서 빼기':'포트폴리오에 담기'}: ${escape(e.title)}" aria-pressed="${e.featured}" title="${e.featured?'포트폴리오에서 빼기':'포트폴리오에 담기'}">${icon('star')}</button></div><h3><button class="card-title-button" data-detail="${e.id}">${escape(e.title)}</button></h3><p class="card-org">${escape(e.organization || e.location || categoryLabel(e))}</p><p class="card-description">${escape(e.description)}</p><div class="card-tags">${tagHtml(e.tags)}</div><div class="card-bottom"><span class="card-date">${icon('calendar')}${escape(period(e))}</span>${award?`<span class="outcome-pill" title="${escape(award)}">${icon('trophy')}${escape(award)}</span>`:''}</div></article>`;
  }
  const addCard = () => `<button class="add-card" data-action="add"><span class="plus-circle">${icon('plus')}</span><strong>경험 추가</strong></button>`;
  function renderExperiences() {
    const list = filtered();
    $('#experience-list').className = `experience-grid ${state.layout === 'list' ? 'list-layout' : ''}`;
    const empty = state.experiences.length === 0 ? `<div class="empty-state"><span>${icon('sprout')}</span><h3>아직 경험이 없어요</h3><button class="button primary" data-action="add">${icon('plus')}경험 추가</button></div>` : `<div class="empty-state"><span>${icon('search')}</span><h3>검색 결과가 없어요</h3><button class="button secondary" data-reset-filter>전체 경험 보기</button></div>`;
    $('#experience-list').innerHTML = list.length ? list.map(card).join('') + addCard() : empty;
    $('#demo-note').hidden = !state.experiences.some(e => e.demo);
    document.querySelectorAll('[data-layout]').forEach(button => {button.classList.toggle('selected', button.dataset.layout === state.layout);button.setAttribute('aria-pressed', String(button.dataset.layout === state.layout));});
  }
  function updateTimelineNavigation() {
    const rail = $('#timeline-list');
    const maxScroll = Math.max(0, rail.scrollWidth - rail.clientWidth);
    $('#timeline-navigation').hidden = rail.clientWidth === 0 || maxScroll <= 1;
    $('#timeline-prev').disabled = rail.scrollLeft <= 1;
    $('#timeline-next').disabled = rail.scrollLeft >= maxScroll - 1;
  }
  function moveTimeline(direction) {
    const column = $('#timeline-list .timeline-year');
    if (!column) return;
    $('#timeline-list').scrollBy({left:direction * (column.getBoundingClientRect().width + 24),
      behavior:matchMedia('(prefers-reduced-motion: reduce)').matches ? 'instant' : 'smooth'});
  }
  function timelineCard(e) {
    const meta = [e.organization,e.role].filter(Boolean).join(' · ');
    const duration = e.ongoing ? `${e.startDate.replaceAll('-','.')} — 진행 중`
      : e.endDate && e.endDate !== e.startDate ? `${e.startDate.replaceAll('-','.')} — ${e.endDate.replaceAll('-','.')}` : '';
    return `<li class="timeline-item"><button class="timeline-card" data-detail="${e.id}" aria-label="${escape(e.title)} · ${escape(e.startDate)}"><span class="timeline-card-top">${badge(e)}<time class="timeline-month" datetime="${escape(e.startDate)}">${Number(e.startDate.slice(5,7))}월 ${Number(e.startDate.slice(8,10))}일</time></span><span class="timeline-title">${escape(e.title)}</span>${meta?`<span class="timeline-meta">${escape(meta)}</span>`:''}${resultSummary(e)?`<span class="timeline-result">${escape(resultSummary(e))}</span>`:''}${duration?`<span class="timeline-period">${escape(duration)}</span>`:''}</button></li>`;
  }
  function renderTimeline() {
    const kind = $('#timeline-type').value;
    const list = state.experiences.filter(e => matchesCategory(e,kind)).sort((a,b) => a.startDate.localeCompare(b.startDate) || a.id-b.id);
    $('#timeline-list').tabIndex = list.length ? 0 : -1;
    if (!list.length) {
      $('#timeline-list').innerHTML = `<div class="empty-state"><span>${icon('timeline')}</span><h3>아직 경험이 없어요</h3><button class="button primary" data-action="add">경험 추가하기</button></div>`;
    } else {
      const years = [...new Set(list.map(e => e.startDate.slice(0,4)))];
      $('#timeline-list').innerHTML = `<div class="timeline-track">${years.map(year => `<section class="timeline-year" aria-labelledby="timeline-year-${year}"><header class="timeline-year-header"><h3 class="year-label" id="timeline-year-${year}">${year}</h3></header><ol class="timeline-items">${list.filter(e => e.startDate.startsWith(year)).map(timelineCard).join('')}</ol></section>`).join('')}</div>`;
    }
    updateTimelineNavigation();
  }
  function renderPortfolio() {
    const p = state.profile, list = state.experiences.filter(e => e.featured).sort((a,b) => b.startDate.localeCompare(a.startDate) || b.id-a.id);
    $('#portfolio-sheet').innerHTML = `<header class="portfolio-header"><h1>${escape(p.name)}</h1><p class="portfolio-headline">${escape(p.headline)}</p>${p.bio?`<p class="portfolio-bio">${escape(p.bio)}</p>`:''}${p.email?`<a class="portfolio-email" href="mailto:${escape(p.email)}">${escape(p.email)}</a>`:''}</header><h2 class="portfolio-section-title">주요 경험 <span>· ${list.length}</span></h2>${list.length ? list.map(e => `<section class="portfolio-item"><div class="portfolio-date">${escape(period(e))}</div><div>${badge(e)}<h3>${escape(e.title)}</h3><p class="item-meta">${escape([e.organization,e.location,e.role].filter(Boolean).join(' · '))}</p>${e.participants?`<p class="item-meta">함께한 사람 · ${escape(e.participants)}</p>`:''}${e.description?`<p>${escape(e.description)}</p>`:''}${resultHtml(e)}${e.outcome?`<p class="item-outcome">${escape(e.outcome)}</p>`:''}<div class="card-tags">${tagHtml(e.tags,30)}</div>${linksHtml(e)}</div></section>`).join('') : '<div class="empty-state"><h3>아직 선택한 경험이 없어요</h3><p>카드의 별표를 눌러 추가하세요.</p></div>'}<footer class="portfolio-end"><span>keep it<img class="portfolio-brand-mark" src="/brand-mark.svg" alt="" width="20" height="20"></span></footer>`;
  }
  function render() {
    renderStats(); renderFilters(); renderExperiences(); renderTimeline(); renderPortfolio();
    $('#sidebar-name').textContent = state.profile.name;
    $('#profile-initial').textContent = [...state.profile.name][0] || '나';
  }
  function navigate(page) {
    if (!['archive','timeline','portfolio','draft'].includes(page)) page = 'archive';
    state.page = page;
    ['archive','timeline','portfolio','draft'].forEach(p => $(`#${p}-page`).hidden = p !== page);
    $('#archive-intro').hidden = page !== 'archive';
    $('#stats').hidden = page === 'portfolio' || page === 'draft';
    $('#breadcrumb-page').textContent = {archive:'나의 경험',timeline:'타임라인',portfolio:'포트폴리오',draft:'자소서 초안'}[page];
    document.querySelectorAll('.primary-nav [data-page]').forEach(b => { b.classList.toggle('active', b.dataset.page===page); if (b.dataset.page === page) b.setAttribute('aria-current','page'); else b.removeAttribute('aria-current'); });
    $('#sidebar').classList.remove('open');
    if (location.hash !== `#${page}`) history.replaceState(null,'',`#${page}`);
    renderFilters();
    if (page === 'draft') renderDraftChoices();
    updateTimelineNavigation();
    window.scrollTo({top:0,behavior:'instant'});
  }
  function configureFields() {
    const form = $('#experience-form'), type = form.elements.type.value;
    const single = ['CERTIFICATE','AWARD'].includes(type), certificate = type === 'CERTIFICATE', paper = type === 'PAPER';
    const custom = type === 'CUSTOM', work = type === 'WORK', competition = ['COMPETITION','AWARD'].includes(type);
    $('#competition-results').hidden = !competition;
    form.elements.placement.disabled = !competition;
    form.elements.awardName.disabled = !competition;
    $('#custom-category-field').hidden = !custom;
    form.elements.customCategory.disabled = !custom;
    form.elements.customCategory.required = custom;
    form.elements.title.placeholder = work ? '예: 백엔드 개발 인턴' : '어떤 경험이었나요?';
    form.elements.organization.placeholder = work ? '회사명과 부서를 입력해주세요' : '학교, 회사, 주최 기관 등';
    form.elements.description.placeholder = work ? '담당 업무와 직접 기여한 내용을 적어주세요.\n어떤 문제를 해결했고, 무엇을 배웠나요?' : '무엇을 했고, 어떤 점을 배웠나요?\n내가 한 일과 느낀 점을 편하게 남겨보세요.';
    $('#ongoing-label').textContent = work ? '현재 재직 중이에요' : '아직 진행 중인 경험이에요';
    $('[data-field="participants"]').hidden = single; form.elements.participants.disabled = single;
    $('[data-field="role"]').hidden = certificate; form.elements.role.disabled = certificate;
    $('#ongoing-field').hidden = single;
    if (single) form.elements.ongoing.checked = false;
    $('#organization-label').textContent = ({WORK:'회사 / 부서',CERTIFICATE:'발급 기관',COMPETITION:'주최 기관',AWARD:'수여 기관',PAPER:'학회 / 학술지',OVERSEAS:'주관 기관'})[type] || '기관 / 소속';
    $('#start-label').textContent = certificate ? '취득일 *' : type === 'AWARD' ? '수상일 *' : work ? '근무 시작일 *' : '시작일 *';
    $('#end-label').textContent = certificate ? '유효기간 종료일 (선택)' : work ? '근무 종료일 (선택)' : '종료일 (선택)';
    $('#role-label').textContent = paper ? '저자 역할 / 기여' : work ? '직무 / 역할' : '나의 역할';
    $('#outcome-label').textContent = ({WORK:'업무 성과 / 배운 점',COMPETITION:'성과 / 배운 점',AWARD:'성과 / 배운 점',CERTIFICATE:'등급 / 합격 결과',PAPER:'게재 / 발표 정보',OVERSEAS:'활동 결과 / 배운 점'})[type] || '결과 / 성과';
    $('#tags-label').textContent = type === 'PROJECT' || work ? '태그 / 사용 기술' : '태그';
    form.elements.endDate.disabled = form.elements.ongoing.checked;
    form.elements.endDate.min = form.elements.startDate.value;
  }
  function openExperience(id=null) {
    const form = $('#experience-form'); form.reset(); state.editing = id;
    const item = state.experiences.find(e => e.id === id);
    const customFilter = state.filter.startsWith('custom:');
    form.elements.type.value = item?.type || (customFilter ? 'CUSTOM' : state.filter === 'ALL' ? 'PROJECT' : state.filter);
    if (!item && customFilter) form.elements.customCategory.value = state.filter.slice(7);
    if (item) Object.entries(item).forEach(([k,v]) => { if (k === 'links') return; const el = form.elements.namedItem(k); if (!el) return; if (el.type === 'checkbox') el.checked = v; else el.value = v ?? ''; });
    renderLinkInputs(linksFor(item || {}));
    $('#experience-dialog-title').textContent = item ? '경험 수정' : '경험 추가';
    $('#save-experience').innerHTML = `저장 ${icon('arrowRight')}`;
    $('#form-error').textContent = ''; configureFields(); showDialog('#experience-dialog');
  }
  function openDetail(id) {
    const e = state.experiences.find(e => e.id === id); if (!e) return;
    const meta = [[e.type === 'WORK' ? '회사 / 부서' : '기관 / 소속',e.organization],['장소',e.location],['함께한 사람',e.participants],[e.type === 'WORK' ? '직무 / 역할' : '나의 역할',e.role]];
    $('#detail-content').innerHTML = `<header class="detail-header">${badge(e)}<button class="icon-button" data-close aria-label="닫기">${icon('close')}</button><h2 id="detail-title">${escape(e.title)}</h2><span class="detail-date">${escape(period(e))}${e.demo?' · 예시 기록':''}</span></header><div class="detail-body"><dl class="detail-meta">${meta.filter(([,v]) => v).map(([l,v]) => `<div><dt>${l}</dt><dd>${escape(v)}</dd></div>`).join('')}</dl>${e.description?`<section class="detail-section"><h3>경험 이야기</h3><p>${escape(e.description)}</p></section>`:''}${resultHtml(e)}${e.outcome?`<section class="detail-section outcome"><h3>결과 / 성과</h3><p>${escape(e.outcome)}</p></section>`:''}<div class="card-tags">${tagHtml(e.tags,30)}</div>${linksFor(e).length?`<section class="detail-section"><h3>관련 링크</h3>${linksHtml(e)}</section>`:''}<p class="detail-date">${e.featured?'★ 포트폴리오에 포함됨':''}</p></div><footer class="modal-footer detail-footer"><button class="delete-button" data-delete="${id}">${icon('trash')}삭제</button><div><button class="button secondary" data-close>닫기</button><button class="button primary" data-edit="${id}">${icon('edit')}경험 수정하기</button></div></footer>`;
    showDialog('#detail-dialog');
  }
  let confirmAction;
  function confirm(title, message, action) {
    $('#confirm-title').textContent = title; $('#confirm-message').textContent = message;
    $('#confirm-dialog .form-error').textContent = ''; confirmAction = action; showDialog('#confirm-dialog');
  }
  $('#confirm-yes').addEventListener('click', async () => {
    const button = $('#confirm-yes'); button.disabled=true;
    try { await confirmAction(); $('#confirm-dialog').close(); }
    catch(e) { $('#confirm-dialog .form-error').textContent=e.message; }
    finally {button.disabled=false;}
  });
  document.addEventListener('click', event => {
    if (event.target.closest('#add-link')) { addLinkRow(); $('#experience-links').lastElementChild.querySelector('input').focus(); return; }
    const removeLink = event.target.closest('[data-remove-link]');
    if (removeLink) { const row = removeLink.closest('.link-row'); if ($('#experience-links').children.length === 1) row.querySelector('input').value = ''; else row.remove(); updateLinkRows(); return; }
    const close = event.target.closest('[data-close]'); if (close) { close.closest('dialog').close(); return; }
    const add = event.target.closest('[data-action="add"]'); if (add) { if(state.loaded) openExperience(); else toast('데이터를 먼저 불러와주세요.',true); return; }
    const nav = event.target.closest('[data-page]'); if (nav) { navigate(nav.dataset.page); return; }
    const filter = event.target.closest('[data-filter]'); if (filter) {state.filter=filter.dataset.filter;navigate('archive');renderFilters();renderExperiences();return;}
    const layout = event.target.closest('[data-layout]'); if (layout) {state.layout=layout.dataset.layout;renderExperiences();return;}
    const reset = event.target.closest('[data-reset-filter]'); if(reset){state.filter='ALL';state.query='';$('#search').value='';renderFilters();renderExperiences();return;}
    const star = event.target.closest('[data-star]'); if(star){run(async () => {const e=state.experiences.find(e=>e.id===Number(star.dataset.star));star.disabled=true;try {const updated=await api(`/experiences/${e.id}`,{method:'PUT',body:JSON.stringify({...e,featured:!e.featured})});state.experiences=state.experiences.map(item=>item.id===updated.id?updated:item);render();toast(updated.featured?'포트폴리오에 담았어요.':'포트폴리오에서 뺐어요.');}finally{star.disabled=false;}});return;}
    const edit = event.target.closest('[data-edit]'); if(edit){openExperience(Number(edit.dataset.edit));return;}
    const del = event.target.closest('[data-delete]'); if(del){const id=Number(del.dataset.delete);confirm('이 경험을 삭제할까요?','삭제한 경험은 되돌릴 수 없어요.',async()=>{await api(`/experiences/${id}`,{method:'DELETE'});state.experiences=state.experiences.filter(e=>e.id!==id);render();toast('경험을 삭제했어요.');});return;}
    const detail = event.target.closest('[data-detail], .experience-card'); if(detail){openDetail(Number(detail.dataset.detail || detail.dataset.id));return;}
    const retry = event.target.closest('[data-retry]'); if(retry) load();
    if (!event.target.closest('#sidebar') && !event.target.closest('#menu-button')) $('#sidebar').classList.remove('open');
  });
  function renderDraftChoices() {
    const checked = new Set([...document.querySelectorAll('#draft-choices input:checked')].map(el => el.value));
    const firstIds = state.experiences.slice(0,30).map(e => String(e.id));
    $('#draft-select-all').checked = firstIds.length > 0 && firstIds.every(id => checked.has(id));
    $('#draft-choices').innerHTML = state.experiences.length ? state.experiences.map(e => `<label class="draft-choice"><input type="checkbox" value="${e.id}" ${checked.has(String(e.id))?'checked':''}><span><strong>${escape(e.title)}</strong><small>${escape(categoryLabel(e))} · ${escape(period(e))}</small></span></label>`).join('') : '<p>먼저 경험을 추가해주세요.</p>';
  }
  $('#draft-select-all').addEventListener('change',event=>{document.querySelectorAll('#draft-choices input').forEach((el,i)=>{el.checked=event.target.checked && i<30;});});
  $('#draft-choices').addEventListener('change',()=>{const boxes=[...document.querySelectorAll('#draft-choices input')];$('#draft-select-all').checked=boxes.length>0 && boxes.slice(0,30).every(el=>el.checked);});
  $('#draft-form').addEventListener('submit', async event => {
    event.preventDefault(); const button=event.currentTarget.querySelector('[type=submit]');
    const ids=[...document.querySelectorAll('#draft-choices input:checked')].map(el=>Number(el.value));
    $('#draft-error').textContent=''; $('#draft-result').hidden=true;
    if (!ids.length || ids.length > 30) { $('#draft-error').textContent='경험을 1개 이상 30개 이하로 선택해주세요.'; return; }
    button.disabled=true; button.textContent='초안을 만들고 있어요…';
    try {
      const result=await api('/drafts',{method:'POST',body:JSON.stringify({question:$('#draft-question').value.trim(),experienceIds:ids})});
      $('#draft-recommendations').innerHTML=result.experiences.length ? result.experiences.map(e=>`<div class="draft-recommendation"><strong>${escape(e.title)}</strong><p>${escape(e.reason)}</p></div>`).join('') : '<p>관련 경험을 찾지 못했어요.</p>';
      $('#draft-text').value=result.draft; $('#draft-result').hidden=false;
    } catch(e) { $('#draft-error').textContent=e.message; }
    finally {button.disabled=false;button.textContent='관련 경험 찾고 초안 만들기';}
  });
  $('#draft-copy').addEventListener('click', async()=>{try{await navigator.clipboard.writeText($('#draft-text').value);toast('초안을 복사했어요.');}catch{toast('초안을 선택해 복사해주세요.',true);}});
  $('#search').addEventListener('input', event=>{state.query=event.target.value.trim();renderExperiences();});
  $('#sort').addEventListener('change',event=>{state.sort=event.target.value;renderExperiences();});
  $('#timeline-type').addEventListener('change',()=>{renderTimeline();$('#timeline-list').scrollTo({left:0,behavior:'instant'});updateTimelineNavigation();});
  $('#timeline-prev').addEventListener('click',()=>moveTimeline(-1));
  $('#timeline-next').addEventListener('click',()=>moveTimeline(1));
  $('#timeline-list').addEventListener('scroll',updateTimelineNavigation,{passive:true});
  window.addEventListener('resize',updateTimelineNavigation);
  $('#type-picker').innerHTML=Object.entries(types).map(([k,v])=>`<label class="type-option ${k === 'CUSTOM' ? 'custom-type-option' : ''}"><input type="radio" name="type" value="${k}" ${k==='PROJECT'?'checked':''} required><span>${icon(v.icon)}${v.label}</span></label>`).join('');
  $('#experience-form').addEventListener('change', configureFields);
  $('#experience-form').addEventListener('submit', async event => {
    event.preventDefault();const form=event.currentTarget, data=Object.fromEntries(new FormData(form));
    data.links=[...$('#experience-links').querySelectorAll('input')].map(input=>input.value.trim()).filter(Boolean);
    data.ongoing=form.elements.ongoing.checked;data.featured=form.elements.featured.checked;
    data.endDate=data.ongoing || !data.endDate ? null : data.endDate;
    data.participants=data.participants || '';data.role=data.role || '';
    data.customCategory=data.type === 'CUSTOM' ? (data.customCategory || '').trim().replace(/\s+/gu,' ') : '';
    const button=$('#save-experience');button.disabled=true;$('#form-error').textContent='';
    try{
      const id=state.editing, updated=await api(`/experiences${id?'/'+id:''}`,{method:id?'PUT':'POST',body:JSON.stringify(data)});
      state.experiences=id?state.experiences.map(e=>e.id===id?updated:e):[...state.experiences,updated];
      if(!id){state.filter='ALL';state.query='';$('#search').value='';$('#timeline-type').value='ALL';}
      render();$('#experience-dialog').close();toast(id?'경험을 다듬었어요.':'새로운 경험을 모았어요.');
    }catch(e){$('#form-error').textContent=e.message;}finally{button.disabled=false;}
  });
  $('#clear-demo').addEventListener('click',()=>confirm('예시 기록을 비울까요?','처음에 들어 있던 가상의 예시만 삭제해요. 직접 추가한 경험은 그대로 남아요.',async()=>{await api('/experiences/demo',{method:'DELETE'});state.experiences=state.experiences.filter(e=>!e.demo);render();toast('이제 나만의 경험을 채워보세요.');}));
  $('#profile-button').addEventListener('click',()=>{if(!state.loaded)return;const form=$('#profile-form');Object.entries(state.profile).forEach(([k,v])=>{if(form.elements[k])form.elements[k].value=v;});$('#profile-form .form-error').textContent='';showDialog('#profile-dialog');});
  $('#profile-form').addEventListener('submit',async event=>{event.preventDefault();const form=event.currentTarget,button=form.querySelector('[type="submit"]');button.disabled=true;try{state.profile=await api('/profile',{method:'PUT',body:JSON.stringify(Object.fromEntries(new FormData(form)))});render();$('#profile-dialog').close();toast('프로필을 저장했어요.');}catch(e){form.querySelector('.form-error').textContent=e.message;}finally{button.disabled=false;}});
  $('#menu-button').addEventListener('click',()=>$('#sidebar').classList.toggle('open'));
  $('#print-button').addEventListener('click',()=>window.print());
  window.addEventListener('hashchange',()=>navigate(location.hash.slice(1)));
  async function load(){
    try{[state.experiences,state.profile]=await Promise.all([api('/experiences'),api('/profile')]);state.loaded=true;render();navigate(location.hash.slice(1)||'archive');}
    catch(e){$('#experience-list').innerHTML=`<div class="error-state"><h3>기록을 불러오지 못했어요.</h3><p>앱이 실행 중인지 확인한 뒤 다시 시도해주세요.</p><button class="button secondary" data-retry>다시 불러오기</button></div>`;toast(e.message,true);}
  }
  load();
})();
