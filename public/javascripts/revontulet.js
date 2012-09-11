"use strict";

$(document).ready(function () {
    $("#searchForm").submit(function () {
        window.location.href  = "/search/" + $("#keyword").val();
        return false;
    });
});
