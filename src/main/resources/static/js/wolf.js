// 显示狼人选择界面
var wolfSelectedUser = null;

function displayWolfSelection(message) {
    const data = JSON.parse(message.message);

    // 获取存活的玩家和狼人列表
    const liveUserList = data.liveUserList;
    const wolfUserList = data.liveWolfList;

    // 清空原有的用户列表
    const wolfUserListDiv = document.getElementById('liveWolfList');
    wolfUserListDiv.innerHTML = '';

    // 1. 显示存活的狼人用户
    wolfUserList.forEach((user, index) => {
        const div = document.createElement('div');
        div.textContent = index > 0 ? `, ${user}` : user;  // 逗号分隔的狼人名称
        wolfUserListDiv.appendChild(div);
    });


    // 2. 显示存活玩家的单选列表（包括狼人）
    const toKillUserListDiv = document.getElementById('toKillUserList');
    toKillUserListDiv.innerHTML = '';  // 清空之前的选择列表

    liveUserList.forEach((user, index) => {
        const div = document.createElement('div');

        // 创建单选框元素
        const radioButton = document.createElement('input');
        radioButton.type = 'radio';
        radioButton.name = 'targetUser'; // 同名的单选框互斥
        radioButton.value = user; // 设置值为玩家名称
        radioButton.id = `radio_${index}`; // 给每个单选框添加唯一的 id

        // 创建标签，点击标签可以选择
        const label = document.createElement('label');
        label.setAttribute('for', `radio_${index}`);
        label.textContent = user;

        // 将单选框和标签添加到 div
        div.appendChild(radioButton);
        div.appendChild(label);

        // 绑定点击事件，点击文本也能选中
        label.addEventListener('click', () => {
            radioButton.checked = true; // 当点击文本时勾选单选框
            sendSelectedMessage(radioButton.value); // 发送选中目标的消息
        });

        // 将 div 添加到目标列表
        toKillUserListDiv.appendChild(div);

        // 给单选框添加事件，选中时触发
        radioButton.addEventListener('change', function () {
            if (this.checked) {
                sendSelectedMessage(this.value); // 选中时发送选中的玩家名称
            }
        });
    });

    // 显示狼人选择区域
    document.getElementById('wolfSelectionSection').style.display = 'block';
}

// 提交狼人选择
function submitWolfSelection() {
    if (wolfSelectedUser) {
        const selectedUserName = wolfSelectedUser;

        // 向服务器发送类型为6的消息，表示最终选择了目标玩家
        const finalMessage = {
            userName:userName,
            type: 6,
            message: selectedUserName
        }
        sendMessageObject(finalMessage);
        // 隐藏选择区域
        document.getElementById('wolfSelectionSection').style.display = 'none';
    } else {
        alert('请先选择一个玩家！');
    }
}

// 发送选中的玩家名称到后端
function sendSelectedMessage(selectedUser) {
    // 发送 WebSocket 消息，消息类型为 5，内容为选中玩家的名称
    wolfSelectedUser = selectedUser;
    const messageDTO = {
        userName:userName,
        type: 5,
        message: selectedUser
    };
    // 通过 WebSocket 发送消息给后端
    sendMessageObject(messageDTO);
}

