const stateSelectorTrain = document.getElementById("admin-state-selector-train");
const stateSelectorSchedule = document.getElementById("admin-state-selector-schedule");
const stateSelectorStation = document.getElementById("admin-state-selector-station");
const stateSelector = document.querySelector(".first-step");
const trainAction = document.getElementById("train-action-methods");
const trainScheduleAction = document.getElementById("train-schedule-action-methods");
const trainStationAction = document.getElementById("train-station-action-methods");

stateSelectorTrain.addEventListener("click", (e) => {
    e.preventDefault();
    stateSelector.hidden = true;
    trainAction.hidden = false;
})

stateSelectorSchedule.addEventListener("click", (e) => {
    e.preventDefault();
    stateSelector.hidden = true;
    trainScheduleAction.hidden = false;
})

stateSelectorStation.addEventListener("click", (e) => {
    e.preventDefault();
    stateSelector.hidden = true;
    trainStationAction.hidden = false;
})
