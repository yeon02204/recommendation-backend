// ===== 🔥 전역 변수 (먼저 선언) =====
const BASE_URL = '';
const API_URL = BASE_URL + '/api/recommend/home';

// ========== 페이지 로드 시 백엔드 리셋 (중복 제거 - 딱 한 번만) ==========
window.addEventListener("load", async () => {
    try {
        await fetch(BASE_URL + "/api/recommend/reset", {
            method: "POST",
            credentials: "include"  // 🔥 쿠키 포함
        });
        console.log("🔄 페이지 로드 - 서버 리셋 완료");
    } catch (err) {
        console.error("❌ 서버 리셋 오류", err);
    }

    handleScrollAnimation();
});


// ========== 뷰 전환 시스템 ==========
const views = {
    landing: document.getElementById('landingView'),
    app: document.getElementById('appView'),
    info: document.getElementById('infoView'),
    guide: document.getElementById('guideView')
};

let currentView = 'landing';
let previousView = 'landing'; // 이전 뷰 기억

function switchView(viewName, autoQuery = null) {
    // 이전 뷰 저장 (guide로 가는 경우만)
    if (viewName === 'guide') {
        previousView = currentView;
    }
    
    // 현재 뷰 페이드 아웃
    views[currentView].classList.add('fade-out');
    
    setTimeout(() => {
        // 모든 뷰 숨기기
        Object.keys(views).forEach(key => {
            views[key].style.display = 'none';
            views[key].classList.remove('fade-out');
        });
        
        // 새 뷰 보이기
        views[viewName].style.display = 'block';
        currentView = viewName;
        
        // 스크롤 최상단으로
        window.scrollTo(0, 0);
        
        // 챗봇 뷰로 전환 시 자동 쿼리 실행
        if (viewName === 'app' && autoQuery) {
            setTimeout(() => {
                userInput.value = autoQuery;
                handleSendMessage();
            }, 300);
        }
    }, 300);
}

// ========== 다크모드 시스템 ==========
const themeToggle = document.getElementById('themeToggle');
const body = document.body;

// localStorage에서 다크모드 설정 불러오기
const savedTheme = localStorage.getItem('theme');
if (savedTheme === 'dark') {
    body.classList.add('dark-mode');
}

themeToggle.addEventListener('click', () => {
    body.classList.toggle('dark-mode');
    const isDark = body.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
});

// ========== 파티 모드 시스템 ==========
const partyToggle = document.getElementById('partyToggle');
const partyFlash = document.getElementById('partyFlash');

partyToggle.addEventListener('click', () => {
    const isActivating = !body.classList.contains('party-mode');
    
    if (isActivating) {
        // ===== 타임라인: 4초의 웅장하고 부드러운 도입 =====
        
        partyToggle.style.transition = 'transform 0.15s cubic-bezier(0.34, 1.56, 0.64, 1)';
        partyToggle.style.transform = 'scale(0.85)';
        
        setTimeout(() => {
            partyToggle.style.transform = 'scale(1)';
        }, 150);
        
        setTimeout(() => {
            document.body.style.transition = 'filter 1s cubic-bezier(0.4, 0, 0.2, 1)';
            document.body.style.filter = 'brightness(0.2) blur(8px)';
        }, 300);
        
        setTimeout(() => {
            partyFlash.classList.add('active');
        }, 1300);
        
        setTimeout(() => {
            body.classList.add('party-mode');
            localStorage.setItem('partyMode', 'active');
            createPartyParticles();
        }, 1800);
        
        setTimeout(() => {
            document.body.style.transition = 'filter 1s cubic-bezier(0.4, 0, 0.2, 1)';
            document.body.style.filter = 'brightness(1.05) blur(0px)';
        }, 2500);
        
        setTimeout(() => {
            document.body.style.transition = 'filter 0.5s ease-out';
            document.body.style.filter = 'brightness(1)';
        }, 3300);
        
        setTimeout(() => {
            partyFlash.classList.remove('active');
        }, 3500);
        
        setTimeout(() => {
            document.body.style.transition = '';
            document.body.style.filter = '';
            partyToggle.style.transition = '';
        }, 4200);
        
    } else {
        // 파티 모드 비활성화
        document.body.style.transition = 'filter 0.5s cubic-bezier(0.4, 0, 0.2, 1)';
        document.body.style.filter = 'brightness(0.3)';
        
        setTimeout(() => {
            body.classList.remove('party-mode');
            localStorage.setItem('partyMode', 'inactive');
            removePartyParticles();
            document.body.style.transition = 'filter 0.4s ease-out';
            document.body.style.filter = 'brightness(1)';
        }, 500);
        
        setTimeout(() => {
            document.body.style.transition = '';
            document.body.style.filter = '';
        }, 900);
    }
});

// ========== 랜딩 페이지 이벤트 ==========

document.getElementById('startButton').addEventListener('click', () => {
    switchView('app');
});

document.querySelectorAll('.example-button').forEach(button => {
    button.addEventListener('click', () => {
        const query = button.getAttribute('data-query');
        switchView('app', query);
    });
});

document.getElementById('openInfoPage').addEventListener('click', () => {
    switchView('info');
});

document.getElementById('openGuidePage').addEventListener('click', () => {
    switchView('guide');
});

// ========== 챗봇 페이지 이벤트 ==========

document.getElementById('backToLanding').addEventListener('click', () => {
    switchView('landing');
});

document.getElementById('guideButtonHeader').addEventListener('click', () => {
    switchView('guide');
});

const menuButton = document.getElementById('menuButton');
const menuDropdown = document.getElementById('menuDropdown');

menuButton.addEventListener('click', (e) => {
    e.stopPropagation();
    menuDropdown.classList.toggle('active');
    console.log('메뉴 버튼 클릭, active 상태:', menuDropdown.classList.contains('active'));
});

document.addEventListener('click', () => {
    if (menuDropdown.classList.contains('active')) {
        menuDropdown.classList.remove('active');
        console.log('메뉴 외부 클릭, 메뉴 닫힘');
    }
});

menuDropdown.addEventListener('click', (e) => {
    e.stopPropagation();
});

document.getElementById('menuRefresh').addEventListener('click', async () => {
    try {
        const response = await fetch(BASE_URL + '/api/recommend/reset', {
            method: 'POST',
            credentials: 'include',  // 🔥 쿠키 포함
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            console.log('✅ 서버 리셋 완료:', data.message);
        } else {
            console.warn('⚠️ 서버 리셋 실패:', data.message);
        }
    } catch (error) {
        console.error('❌ 서버 리셋 오류:', error);
    }
    
    resetChat();
    menuDropdown.classList.remove('active');
});

document.getElementById('menuInfo').addEventListener('click', () => {
    switchView('info');
});

document.getElementById('menuGuide').addEventListener('click', () => {
    switchView('guide');
});

document.getElementById('menuHome').addEventListener('click', () => {
    switchView('landing');
});

// ========== 개발자 정보 페이지 이벤트 ==========

document.getElementById('backFromInfo').addEventListener('click', () => {
    switchView('landing');
});

const githubInput = document.getElementById('githubInput');
const githubButton = document.getElementById('githubButton');
const githubLink = document.getElementById('githubLink');

const savedGithubUrl = localStorage.getItem('githubUrl');
if (savedGithubUrl) {
    githubInput.value = savedGithubUrl;
    githubLink.href = savedGithubUrl;
    githubLink.style.display = 'inline-flex';
}

githubButton.addEventListener('click', () => {
    const url = githubInput.value.trim();
    if (url) {
        localStorage.setItem('githubUrl', url);
        githubLink.href = url;
        githubLink.style.display = 'inline-flex';
        alert('GitHub 링크가 저장되었습니다!');
    }
});

// ========== 사용 가이드 페이지 이벤트 ==========

document.getElementById('backFromGuide').addEventListener('click', () => {
    switchView(previousView);
});

// ========== 스크롤 애니메이션 ==========
function handleScrollAnimation() {
    const elements = document.querySelectorAll('.fade-in');
    const windowHeight = window.innerHeight;
    
    elements.forEach(element => {
        const elementTop = element.getBoundingClientRect().top;
        
        if (elementTop < windowHeight - 100) {
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }
    });
}

window.addEventListener('scroll', handleScrollAnimation);

// ========================================================
// ========== 챗봇 로직 ==========
// ========================================================

const messagesContainer = document.getElementById('messagesContainer');
const userInput = document.getElementById('userInput');
const sendButton = document.getElementById('sendButton');
const cardsDeckContainer = document.getElementById('cardsDeckContainer');
const cardsNavigation = document.getElementById('cardsNavigation');
const prevButton = document.getElementById('prevButton');
const nextButton = document.getElementById('nextButton');
const cardIndicators = document.getElementById('cardIndicators');
const cardCount = document.getElementById('cardCount');

let currentProducts = [];
let currentCardIndex = 0;
let initialBotMessageCleared = false;

const BOT_AVATAR_NORMAL = './dog-normal.png';
const BOT_AVATAR_LOADING = './dog-loading.png';
const BOT_AVATAR_SUCCESS = './dog-success.png';

sendButton.addEventListener('click', handleSendMessage);
userInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSendMessage();
});

prevButton.addEventListener('click', showPreviousCard);
nextButton.addEventListener('click', showNextCard);

// ===== 메시지 전송 =====
function handleSendMessage() {
    const message = userInput.value.trim();
    if (!message) return;

    addUserMessage(message);
    userInput.value = '';

    showLoadingMessage();
    fetchRecommendations(message);
}

// ===== API 호출 =====
async function fetchRecommendations(userInputText) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            credentials: 'include',  // 🔥 쿠키 포함 - 세션 유지 핵심
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userInput: userInputText
            })
        });

        const data = await response.json();

        removeLoadingMessage();
        handleServerResponse(data);

    } catch (error) {
        console.error(error);
        removeLoadingMessage();
        showErrorMessage();
    }
}

// ===== 서버 응답 처리 =====
function handleServerResponse(data) {

    clearInitialBotMessageIfNeeded();

    const type =
        data.type ??
        data.decisionType;

    const message =
        data.message ??
        data.content ??
        '';

    const items =
        data.items ??
        data.products ??
        [];

    if (type === 'REQUERY' || type === 'INVALID') {
        addBotMessage(message || '조금만 더 알려줄래?', BOT_AVATAR_NORMAL);
        clearCards();
        return;
    }

    if (type === 'RECOMMEND') {
        addBotMessage(message || '이 상품들 어때?', BOT_AVATAR_SUCCESS);

        const products = items.map(item => ({
            id: item.productId,
            name: item.title,
            price: item.price
                ? `${Number(item.price).toLocaleString()}원`
                : '',
            image: item.imageUrl,
            mall: item.mallName,
            link: item.link,
            reason: item.explanation || '추천 이유를 생성 중이에요.'
        }));

        showRecommendations(products);
    }
}

// ===== HTML 초기 봇 메시지 제거 =====
function clearInitialBotMessageIfNeeded() {
    if (initialBotMessageCleared) return;

    const botMessages = messagesContainer.querySelectorAll('.bot-message');

    if (botMessages.length === 1) {
        const bubble = botMessages[0].querySelector('.message-bubble');
        if (bubble && bubble.textContent.trim() === '안녕! 뭘 찾아드릴까요?') {
            botMessages[0].remove();
            initialBotMessageCleared = true;
        }
    }
}

// ===== 메시지 UI =====
function addUserMessage(text) {
    const div = document.createElement('div');
    div.className = 'message user-message';
    div.innerHTML = `<div class="message-bubble">${text}</div>`;
    messagesContainer.appendChild(div);
    scrollToBottom();
}

function addBotMessage(text, avatar) {
    const div = document.createElement('div');
    div.className = 'message bot-message';
    div.innerHTML = `
        <img src="${avatar}" class="bot-avatar">
        <div class="message-bubble">${text}</div>
    `;
    messagesContainer.appendChild(div);
    scrollToBottom();
}

function showLoadingMessage() {
    const div = document.createElement('div');
    div.id = 'loading-message';
    div.className = 'message bot-message';
    div.innerHTML = `
        <img src="${BOT_AVATAR_LOADING}" class="bot-avatar">
        <div class="message-bubble loading-dots">
            <span></span><span></span><span></span>
        </div>
    `;
    messagesContainer.appendChild(div);
    scrollToBottom();
}

function removeLoadingMessage() {
    document.getElementById('loading-message')?.remove();
}

function showErrorMessage() {
    addBotMessage('서버랑 연결이 끊겼어. 다시 시도해줄래?', BOT_AVATAR_NORMAL);
}

// ===== 카드 처리 =====
function showRecommendations(products) {
    currentProducts = products;
    currentCardIndex = 0;
    displayCardDeck();
    updateCardCount();
}

function clearCards() {
    currentProducts = [];
    currentCardIndex = 0;
    cardsDeckContainer.innerHTML =
        '<div class="no-cards-message">조건을 더 알려주면 추천해줄게!</div>';
    cardsNavigation.style.display = 'none';
    cardCount.textContent = '';
}

function displayCardDeck() {
    cardsDeckContainer.innerHTML = '';

    if (currentProducts.length === 0) {
        clearCards();
        return;
    }

    const deck = document.createElement('div');
    deck.className = 'card-deck';

    currentProducts.forEach((product, index) => {
        deck.appendChild(createProductCard(product, index));
    });

    cardsDeckContainer.appendChild(deck);
    cardsNavigation.style.display = 'flex';

    updateCardPositions();
    updateIndicators();
    updateNavigationButtons();
}

function createProductCard(product, index) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.dataset.index = index;

    card.innerHTML = `
        <div class="card-content">
            <img src="${product.image}" class="card-image">
            <div class="card-info">
                <div class="card-title">${product.name}</div>
                <div class="card-price">${product.price}</div>
                <div class="card-mall">${product.mall}</div>
            </div>
            <div class="card-reason">
                <div class="reason-label">추천 이유</div>
                <div class="reason-text">${product.reason}</div>
            </div>
        </div>
    `;

    card.addEventListener('click', () => {
        if (index === currentCardIndex) {
            window.open(product.link, '_blank');
        }
    });

    return card;
}

// ===== 카드 네비 =====
function updateCardPositions() {
    document.querySelectorAll('.product-card').forEach(card => {
        const idx = Number(card.dataset.index);
        const diff = idx - currentCardIndex;

        card.className = 'product-card';

        if (diff === 0) card.classList.add('active');
        else if (diff === 1) card.classList.add('stacked', 'stacked-1');
        else if (diff === 2) card.classList.add('stacked', 'stacked-2');
        else if (diff >= 3) card.classList.add('stacked', 'stacked-3');
        else if (diff === -1) card.classList.add('prev-1');
        else if (diff <= -2) card.classList.add('prev-2');
    });
}

function showPreviousCard() {
    if (currentCardIndex > 0) {
        currentCardIndex--;
        updateAll();
    }
}

function showNextCard() {
    if (currentCardIndex < currentProducts.length - 1) {
        currentCardIndex++;
        updateAll();
    }
}

function updateAll() {
    updateCardPositions();
    updateIndicators();
    updateNavigationButtons();
    updateCardCount();
}

function updateIndicators() {
    cardIndicators.innerHTML = '';
    currentProducts.forEach((_, i) => {
        const dot = document.createElement('div');
        dot.className = 'indicator';
        if (i === currentCardIndex) dot.classList.add('active');
        cardIndicators.appendChild(dot);
    });
}

function updateNavigationButtons() {
    prevButton.disabled = currentCardIndex === 0;
    nextButton.disabled = currentCardIndex === currentProducts.length - 1;
}

function updateCardCount() {
    cardCount.textContent =
        currentProducts.length > 0
            ? `(${currentCardIndex + 1}/${currentProducts.length})`
            : '';
}

// ===== 스크롤 =====
function scrollToBottom() {
    setTimeout(() => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }, 50);
}

// ===== 채팅 초기화 =====
function resetChat() {
    messagesContainer.innerHTML = '';
    
    const initialMessage = document.createElement('div');
    initialMessage.className = 'message bot-message';
    initialMessage.innerHTML = `
        <img src="${BOT_AVATAR_NORMAL}" alt="봇" class="bot-avatar">
        <div class="message-bubble">
            안녕! 뭘 찾아드릴까요?
        </div>
    `;
    messagesContainer.appendChild(initialMessage);
    
    clearCards();
    initialBotMessageCleared = false;
    userInput.value = '';
    
    console.log('채팅이 초기화되었습니다.');
}

// ===== 파티 모드 파티클 시스템 =====
function createPartyParticles() {
    const container = document.createElement('div');
    container.id = 'party-particles';
    container.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none;
        z-index: 2;
    `;
    
    for (let i = 0; i < 150; i++) {
        const particle = document.createElement('div');
        particle.className = 'party-particle';
        
        const x = Math.random() * 100;
        const y = Math.random() * 100;
        const size = 3 + Math.random() * 8;
        
        const colors = [
            '#ff006e', '#bf00ff', '#00f5ff', '#ccff00', 
            '#ff1493', '#00ff00', '#ff00ff', '#00ffff',
            '#ff0080', '#8b00ff', '#00ff88', '#ffff00'
        ];
        const color = colors[Math.floor(Math.random() * colors.length)];
        
        const delay = Math.random() * 1.5;
        const twinkleSpeed = 0.3 + Math.random() * 0.4;
        const floatSpeed = 2 + Math.random() * 2;
        
        particle.style.cssText = `
            position: absolute;
            left: ${x}%;
            top: ${y}%;
            width: ${size}px;
            height: ${size}px;
            border-radius: 50%;
            background: ${color};
            box-shadow: 
                0 0 ${size * 4}px ${color},
                0 0 ${size * 8}px ${color};
            animation: 
                particleTwinkle ${twinkleSpeed}s ease-in-out ${delay}s infinite,
                particleFloat ${floatSpeed}s ease-in-out ${delay}s infinite,
                particleSpin ${floatSpeed * 1.5}s linear ${delay}s infinite;
            filter: brightness(1.5);
        `;
        
        container.appendChild(particle);
    }
    
    document.body.appendChild(container);
}

function removePartyParticles() {
    const container = document.getElementById('party-particles');
    if (container) {
        container.remove();
    }
}