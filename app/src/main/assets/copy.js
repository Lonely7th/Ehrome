function copy_() {
    removeCopy_(document.getElementsByTagName("div"));
    removeCopy_(document.getElementsByTagName("p"));
    removeCopy_(document.getElementsByTagName("body"));
}

function removeCopy_(ele) {
    var copyCss = "user-select: text !important;-webkit-user-select: text !important;-moz-user-select: text !important;-ms-user-select: text !important;-khtml-user-select: text !important;";
    var len = ele.length;
    for (var i = 0; i < len; i++) {
        var sty = ele[i].getAttribute("style");
        if (sty == null) {
            sty = copyCss;
        } else {
            sty += copyCss;
        }
        ele[i].setAttribute("style", sty);
        ele[i].removeAttribute("onselectstart");
        ele[i].removeAttribute("oncontextmenu");
        ele[i].removeAttribute("oncopy");
        ele[i].removeAttribute("onpaste");
        ele[i].removeAttribute("onselect");
        ele[i].removeAttribute("onbeforecopy");
    }
}
copy_();