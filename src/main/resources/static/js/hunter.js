// 显示猎人攻击选择界面
var hunterKillType;

function displayHunterKillSelection(message) {
    const data = JSON.parse(message.message);  // 获取玩家列表数据
    hunterKillType = message.type;
    // 获取下拉框元素
    const killSelect = document.getElementById("killSelect");

    // 清空现有选项
    killSelect.innerHTML = '';

    // 填充可选玩家列表（这里的 data 是玩家名称数组）
    data.forEach(userName => {
        const option = document.createElement("option");
        option.value = userName;  // 玩家名称作为值
        option.textContent = userName;  // 玩家名称显示在下拉框中
        killSelect.appendChild(option);
    });

    // 显示猎人选择攻击目标的区域
    document.getElementById("hunterKillSection").style.display = "block";
}

// 提交猎人选择攻击的目标
function submitHunterKill() {
    document.getElementById("hunterKillSection").style.display = "none"; // 隐藏猎人操作框

    const killSelect = document.getElementById("killSelect");
    const selectedUser = killSelect.value;  // 获取选中的玩家名称

    if (!selectedUser) {
        alert("请选择一个玩家！");
        return;
    }

    // 构造消息并发送给后端，消息类型为 12（猎人提交枪杀目标）
    const message = {
        userName: userName,
        type: hunterKillType,
        message: selectedUser  // 选中的玩家名称
    };

    // 发送消息到服务器
    sendMessageObject(message)
        .then(() => {
            // 提交成功后隐藏猎人操作框
        })
        .catch(error => {
            console.error('提交失败:', error);
            alert("提交失败，请稍后再试！");
        });
}

