// 重置游戏UI
// 重置游戏UI
function resetGameUI() {
    // 隐藏所有动态操作区域
    hideAllSections();

    // 重置玩家身份区域和游戏消息
    resetGameStatus();

    // 隐藏重开按钮区域
    document.getElementById("restartButtonSection").style.display = "none";

    // 恢复准备入夜按钮
    displayNightReadyButton();
    // 恢复其他初始化按钮、区域（如身份显示等）
    displayIdentity({message: "等待开始游戏..."});
    document.getElementById("identitySection").style.display = "block";
}


// 清空操作区域

function hideAllSections() {
    document.getElementById("villagerVoteSection").style.display = "none";
    document.getElementById("wolfSelectionSection").style.display = "none";
    document.getElementById("prophetSelectionSection").style.display = "none";
    document.getElementById("prophetConfirmSection").style.display = "none";
    document.getElementById("witchSelectionSection").style.display = "none";
    document.getElementById("hunterKillSection").style.display = "none";
    document.getElementById("identitySection").style.display = "none";
}
// 重置游戏界面信息
function resetGameStatus() {
    // 重置身份信息
    document.getElementById("identityInfo").textContent = "正在加载身份...";

    // 清空消息
    const messages = document.getElementById("messages");
    messages.innerHTML = '';

    // 清空死亡信息区域
    const deathInfoElement = document.getElementById("deathInfo");
    deathInfoElement.textContent = '';
}

// 重开游戏函数
function restartGame() {
    // 隐藏重开按钮区域
    document.getElementById("restartButtonSection").style.display = "none"; // Hide restart button after clicked

    // 向后端发送请求，请求重开游戏
    fetch('/game/restart', {
        method: 'GET',  // Trigger the restart game endpoint
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                console.log('游戏已重开');
                // Add message to show that the game has restarted
                addMessage("系统：游戏已重开，所有玩家请重置界面。", 'system');
            } else {
                alert("重开游戏失败，服务器错误！");
            }
        })
        .catch(error => {
            console.error('请求失败:', error);
            alert("网络错误，无法重开游戏！");
        });
}
