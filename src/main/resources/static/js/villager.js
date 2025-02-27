// 显示村民投票选择界面
function displayVillagerVoteSelection(message) {
    const data = JSON.parse(message.message);  // 获取玩家列表数据

    // 获取投票选择下拉框元素
    const voteSelect = document.getElementById("voteSelect");

    // 清空现有选项
    voteSelect.innerHTML = '';

    // 填充可选玩家列表（这里的 data 是玩家名称数组）
    data.forEach(userName => {
        const option = document.createElement("option");
        option.value = userName;  // 玩家名称作为值
        option.textContent = userName;  // 玩家名称显示在下拉框中
        voteSelect.appendChild(option);
    });

    // 显示村民投票选择的区域
    document.getElementById("villagerVoteSection").style.display = "block";
}
// 提交村民投票
function submitVote() {
    const voteSelect = document.getElementById("voteSelect");
    const selectedUser = voteSelect.value;  // 获取选中的玩家名称

    if (!selectedUser) {
        alert("请选择一个玩家进行投票！");
        return;
    }

    // 构造消息并发送给后端，消息类型为 13（村民提交投票）
    const message = {
        userName: userName,
        type: 13,  // 这里是 13，表示村民投票
        message: selectedUser  // 选中的玩家名称
    };

    // 发送消息到服务器
    sendMessageObject(message);

    // 隐藏村民投票操作框
    document.getElementById("villagerVoteSection").style.display = "none";
}

// 提交弃票
function submitAbstain() {
    // 构造消息并发送给后端，消息类型为 13（弃票）
    const message = {
        userName: userName,
        type: 13,  // 这里是 13，表示弃票
        message: ""  // 空值表示弃票
    };

    // 发送消息到服务器
    sendMessageObject(message);

    // 隐藏村民投票操作框
    document.getElementById("villagerVoteSection").style.display = "none";
}
