"use strict";

$(document).ready(function () {
    $("#searchForm").submit(function () {
        if ($("#keyword").val()) window.location.href  = "/search/" + $("#keyword").val();
        return false;
    });
});
