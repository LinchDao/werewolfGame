let failCount = 0;
let userName;
let identityVisible = true; // 标记身份区域是否显示
// 假设你需要在页面加载时启动定时轮询
window.onload = function () {
    // 定义轮询间隔，单位为毫秒
    const pollingInterval = 2000; // 每5秒发送一次请求
    // 启动轮询
    startPolling(pollingInterval);
    userName = getUserNameFromServer();
};

function startPolling(interval) {
    // 使用 setInterval 定时轮询接口
    setInterval(async () => {
        try {
            // 发起 GET 请求，假设接口是 POST 方法，且需要用户的 session 信息
            const response = await fetch('/user/msg/receive', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({userName: userName})
            });

            if (response.ok) {
                const messages = await response.json();
                console.log('Received messages:', messages);
                handleMessages(messages);
            } else {
                console.error('Failed to fetch messages:', response.statusText);
            }
        } catch (error) {
            failCount++;
            if (failCount > 10) {
                window.location.href = '/join';  // 跳转到游戏页面
            }
            console.error('Error fetching messages:', error);
        }
    }, interval);
}

// 处理收到的消息
function handleMessages(messages) {
    if (messages && messages.length > 0) {
        messages.forEach((msg) => {
            console.log(`Message from ${msg.userName}: ${msg.message}`);

            // 这里直接使用 msg，因为它已经是一个对象，不需要再次解析
            const data = msg;

            if (data.type === 1) {  // 身份消息
                displayIdentity(data);
            } else if (data.type === 2) {  // 准备入夜提示
                displayNightReadyButton();
            } else if (data.type === 4) {  // 狼人选择杀人
                displayWolfSelection(data);
            } else if (data.type === 8) {  // 预言家选择查验
                displayProphetSelection(data);
            } else if (data.type === 9) {  // 预言家查验结果
                displayProphetConfirm(data);
            } else if (data.type === 10) {  // 女巫选择救人或毒人
                displayWitchSelection(data);
            } else if (data.type === 12 || data.type === 14) {  // 接收到猎人攻击选择消息
                displayHunterKillSelection(data);
            } else if (data.type === 13) {  // 接收到村民投票操作框消息
                displayVillagerVoteSelection(data);
            } else if (data.type === 20) {  // 角色死亡
                handleCharacterDeath(data); // 调用死亡处理函数
            } else if (data.type === 21) {
                addMessage(data.message, 'system');
                displayRestartGameButton();
            } else if (data.type === 22) {  // 游戏重开
                resetGameUI();  // 重置所有界面状态
                addMessage("系统：游戏已重开。", 'system');
            } else if (data.type === 5) {
                addMessage(data.message, 'system');
            } else {
                addMessage(data.message, 'system');
            }
        });
    } else {
        console.log('No new messages');
    }
}


// 使用 Fetch API 获取用户名
async function getUserNameFromServer() {
    const response = await fetch('/user/get/username', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (response.ok) {
        const userName2 = await response.text();  // 服务器返回的是用户名字符串
        userName = userName2
        if (userName == null || userName.length == 0) {
            window.location.href = '/join';  // 跳转到游戏页面
        }
    } else {
        throw new Error('Failed to fetch username');
    }
}

// =======================================


// 函数 1：接受对象作为参数
function sendMessageObject(message) {
    return fetch('/user/msg/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    }).then(data => {
        console.log('Success:', data);
        return data;
    })
        .catch(error => {
            console.error('Error:', error);
            throw error;
        });
}

// 函数 2：接受 type 和 message 作为参数
function sendMessage(type, message) {
    const messageObj = {type: type, message: message, userName: userName};
    return sendMessageObject(messageObj); // 复用第一个函数
}

// 在页面上显示消息
function addMessage(message, type) {
    const messages = document.getElementById("messages");
    const li = document.createElement("li");
    li.className = 'message ' + type;
    li.textContent = message;
    messages.appendChild(li);
}

// 切换身份区域的显示或隐藏
function toggleIdentitySection() {
    const identityInfo = document.getElementById("identityInfo");
    identityVisible = !identityVisible; // 改变显示状态
    identityInfo.style.display = identityVisible ? "block" : "none";
}

// 展示身份信息
function displayIdentity(message) {
    const identityElement = document.getElementById("identityInfo");
    identityElement.textContent = message.message;
}

// 点击“准备入夜”按钮，向后台发送消息
function readyForNight() {
    const messageDTO = {
        userName: userName,
        type: 3,  // 发送准备入夜类型的消息
        message: "玩家准备入夜"
    };
    sendMessageObject(messageDTO);
    addMessage("系统：你已准备入夜", 'system');
    document.getElementById("nightReadySection").style.display = "none";
}

// 显示准备入夜按钮
// 显示准备入夜按钮
function displayNightReadyButton() {
    const nightReadyButton = document.getElementById("nightReadySection");
    if (nightReadySection) {
        nightReadyButton.style.display = "block";  // 显示准备入夜按钮
    } else {
        console.error("准备入夜按钮未找到！");
    }
}


function handleCharacterDeath(data) {
    // 假设 data.message 是死亡的玩家名称
    const deadUser = data.message;

    // 隐藏操作区域
    hideAllSections()

    // 显示死亡信息（可以在某个区域展示）
    const deathInfoElement = document.getElementById("deathInfo");
    deathInfoElement.textContent = `${deadUser} 已淘汰，操作区域已被禁用。`;

    // 如果需要显示某些死亡后不可进行的操作，可以在这里进行控制
    // 比如禁用所有操作按钮
}

// 显示重开游戏按钮
function displayRestartGameButton() {
    // 显示重开按钮
    const restartButtonSection = document.getElementById("restartButtonSection");
    restartButtonSection.style.display = "block";  // 显示重开按钮区域
}

