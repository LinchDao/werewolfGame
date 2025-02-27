function displayWitchSelection(message) {
    const data = JSON.parse(message.message);
    document.getElementById("witchSelectionSection").style.display = "block"; // Show operation box

    console.log(data); // Print data for confirmation

    // Check if the data is valid

    // Show the target information (player attacked by werewolves)
    document.getElementById("targetInfo").textContent = `被狼人杀害的玩家是：${data.target}`;

    // Handle the save option
    const saveOption = document.getElementById("saveOption");
    if (data.canSave) {
        saveOption.style.display = "block";  // Show save option
    } else {

        saveOption.style.display = "none";  // 隐藏救人选项
        saveNo.checked = true; // 隐藏时将“否”选项设置为选中
        saveYes.checked = false; // 确保“是”选项未选中
    }

    // Handle the poison option
    const poisonOption = document.getElementById("poisonOption");
    const poisonYes = document.getElementById("poisonYes");
    const poisonNo = document.getElementById("poisonNo");
    const poisonSelect = document.getElementById("poisonSelect");

    if (data.canPoison) {
        poisonOption.style.display = "block";  // Show poison option

        // Clear existing options from the dropdown
        poisonSelect.innerHTML = '';
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.textContent = "选择玩家";
        poisonSelect.appendChild(defaultOption);

        // Fill the poison target dropdown with available targets
        data.poisonTargets.forEach(user => {
            const option = document.createElement("option");
            option.value = user;
            option.textContent = user;
            poisonSelect.appendChild(option);
        });
    } else {
        poisonOption.style.display = "none";  // 隐藏毒人选项
        poisonNo.checked = true; // 隐藏时将“否”选项设置为选中
        poisonYes.checked = false; // 确保“是”选项未选中
    }

    // Handle the behavior of the poison choice (show/hide the select box)
    poisonYes.addEventListener('click', function () {
        poisonSelect.style.display = "block";  // Show the poison selection dropdown
    });

    poisonNo.addEventListener('click', function () {
        poisonSelect.style.display = "none";  // Hide the poison selection dropdown
    });
}

function submitWitchSelection() {
    const saveChoice = document.querySelector('input[name="save"]:checked');  // Get the selected save option
    const poisonChoice = document.getElementById("poisonSelect").value;  // Get the selected poison target

    // Prepare the data to send to the server
    const witchAction = {
        save: saveChoice ? saveChoice.value === "true" : false,
        poisonTarget: poisonChoice !== "" ? poisonChoice : null
    };

    // Send data to the server
    sendMessage(10, JSON.stringify(witchAction))  // Send message to backend

    // Hide the witch selection box
    document.getElementById("witchSelectionSection").style.display = "none";  // Hide the witch operation box
}
