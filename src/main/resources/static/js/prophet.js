// 预言家选择要查验的玩家
function displayProphetSelection(message) {
    // 获取预言家可查验的玩家列表
    const userNameList = JSON.parse(message.message); // 解析服务器传来的玩家列表

    // 显示预言家选择区
    const prophetSelectionSection = document.getElementById("prophetSelectionSection");
    prophetSelectionSection.style.display = "block";

    // 获取选择框容器
    const toCheckUserListDiv = document.getElementById("toCheckUserList");
    toCheckUserListDiv.innerHTML = ""; // 清空之前的内容

    // 创建单选框列表
    userNameList.forEach(userName => {
        const label = document.createElement("label");
        const input = document.createElement("input");
        input.type = "radio";
        input.name = "prophetSelection"; // 单选框的名称相同，表示只有一个能选中
        input.value = userName;
        label.textContent = `查验玩家: ${userName}`;
        label.prepend(input);
        toCheckUserListDiv.appendChild(label);
        toCheckUserListDiv.appendChild(document.createElement("br"));
    });
}

// 提交预言家选择
function submitProphetSelection() {
    const selectedPlayer = getSelectedPlayer("toCheckUserList");
    if (selectedPlayer) {
        // 向服务器发送预言家选择的消息
        sendMessageObject({
            userName: userName,
            type: 8,
            message: selectedPlayer
        });

        // 隐藏选择区域
        document.getElementById("prophetSelectionSection").style.display = "none";

        // 显示“正在查询，请稍后...”消息
        const waitingMessage = document.getElementById("waitingMessage");
        waitingMessage.style.display = "block";  // 显示查询提示
    }
}

// 获取选中的玩家名称
function getSelectedPlayer(listId) {
    // 获取选中的单选框的值（即被查验的玩家名称）
    const selected = document.querySelector(`#${listId} input:checked`);
    return selected ? selected.value : "";
}

// 显示查验结果
function displayProphetConfirm(message) {
    const confirmMessage = message.message;  // 例如: "【玩家1】的身份是【狼人】"

    // 隐藏“正在查询，请稍后...”消息
    const waitingMessage = document.getElementById("waitingMessage");
    waitingMessage.style.display = "none";  // 隐藏查询提示

    // 显示查验结果
    const prophetConfirmSection = document.getElementById("prophetConfirmSection");
    prophetConfirmSection.style.display = "block";

    const confirmMessageDiv = document.getElementById("confirmMessage");
    confirmMessageDiv.textContent = confirmMessage;

    // 启用确认按钮
    const confirmButton = document.getElementById("confirmButton");
    confirmButton.style.display = "inline-block";  // 显示确认按钮
}

// 提交确认选择
function submitProphetConfirm() {
    // 向服务器发送确认消息
    sendMessageObject({
        userName: userName,
        type: 9, // 类型为9，表示预言家确认
        message: "confirm"  // 可附带其他确认内容，例如确认已查看
    });

    // 隐藏确认区域
    document.getElementById("prophetConfirmSection").style.display = "none";
}
