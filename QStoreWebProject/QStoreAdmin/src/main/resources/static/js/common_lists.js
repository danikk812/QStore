function clearFilter() {
    window.location = clearFilterURL;
}

function showDeleteConfirmModal(link, entityName) {
    entityId = link.attr("entityId");

    $("#deleteButton").attr("href", link.attr("href"));
    $("#confirmBody").text("Are you sure you want to delete " + entityName + " with id " + entityId + "?");
    $("#deleteConfirmModal").modal();
}