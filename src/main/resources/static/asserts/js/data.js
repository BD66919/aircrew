$(function () {

    function sleep(numberMillis) {
        var now = new Date();
        var exitTime = now.getTime() + numberMillis;
        while (true) {
            now = new Date();
            if (now.getTime() > exitTime)
                return;
        }
    }

    //飞行按钮jquery
    $("#addLastAir").on("hidden.bs.modal", function () {
        document.getElementById("LastAirStatus").innerHTML = "";
        $("#LastAirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addLastAirFile').click(function () {
        document.getElementById("LastAirStatus").innerHTML = "";
        $("#LastAirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addLastAirSubmit').click(function () {
        var $file1 = $("input[name='addLastAirFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addLastAirFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addLastAir",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#LastAirStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#LastAirStatus").css('color', "green");
                    $("#LastAirStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#LastAirStatus").html(msg);
            }
        });
    });

    //飞行按钮jquery
    $("#addAir").on("hidden.bs.modal", function () {
        document.getElementById("AirStatus").innerHTML = "";
        $("#AirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addAirFile').click(function () {
        document.getElementById("AirStatus").innerHTML = "";
        $("#AirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addAirSubmit').click(function () {
        var $file2 = $("input[name='addAirFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file2 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addAirFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addAir",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#AirStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#AirStatus").css('color', "green");
                    $("#AirStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#AirStatus").html(msg);
            }
        });
    });

    //人力按钮jquery
    $("#addNextAir").on("hidden.bs.modal", function () {
        document.getElementById("NextAirStatus").innerHTML = "";
        $("#NextAirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addNextAirFile').click(function () {
        document.getElementById("NextAirStatus").innerHTML = "";
        $("#NextAirStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addNextAirSubmit').click(function () {
        var $file1 = $("input[name='addNextAirFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addNextAirFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addNextAir",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#NextAirStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#NextAirStatus").css('color', "green");
                    $("#NextAirStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000);
                }
                $("#NextAirStatus").html(msg);
            }
        });
    });

    //人力按钮jquery
    $("#addLastMp").on("hidden.bs.modal", function () {
        document.getElementById("LastMpStatus").innerHTML = "";
        $("#LastMpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addLastMpFile').click(function () {
        document.getElementById("LastStatus").innerHTML = "";
        $("#LastMpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addLastMpSubmit').click(function () {
        var $file1 = $("input[name='addLastMpFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addLastMpFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addLastMp",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#LastMpStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#LastMpStatus").css('color', "green");
                    $("#LastMpStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#LastMpStatus").html(msg);
            }
        });
    });

    //人力按钮jquery
    $("#addMp").on("hidden.bs.modal", function () {
        document.getElementById("MpStatus").innerHTML = "";
        $("#MpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addMpFile').click(function () {
        document.getElementById("MpStatus").innerHTML = "";
        $("#MpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addMpSubmit').click(function () {
        var $file1 = $("input[name='addMpFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addMpFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addMp",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#MpStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#MpStatus").css('color', "green");
                    $("#MpStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#MpStatus").html(msg);
            }
        });
    });

    //人力按钮jquery
    $("#addNextMp").on("hidden.bs.modal", function () {
        document.getElementById("NextMpStatus").innerHTML = "";
        $("#NextMpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addNextMpFile').click(function () {
        document.getElementById("NextMpStatus").innerHTML = "";
        $("#NextMpStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addNextMpSubmit').click(function () {
        var $file1 = $("input[name='addNextMpFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addNextMpFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addNextMp",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#NextMpStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#NextMpStatus").css('color', "green");
                    $("#NextMpStatus").html(msg);
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#NextMpStatus").html(msg);
            }
        });
    });

    //财务按钮jquery
    $("#addFl").on("hidden.bs.modal", function () {
        document.getElementById("FlStatus").innerHTML = "";
        $("#FlStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addFlFile').click(function () {
        document.getElementById("FlStatus").innerHTML = "";
        $("#FlStatus").css('color', "red");
        $('.progress > div').css('width', "0%");
    });

    $('#addFlSubmit').click(function () {
        var $file1 = $("input[name='addFlFile']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 === "") {
            alert("请选择上传的目标文件! ");
            return false;
        }
        var type = "file";
        var formData = new FormData();//这里需要实例化一个FormData来进行文件上传
        formData.append(type, $("#addFlFile")[0].files[0]);
        $.ajax({
            type: "post",
            url: "/aircrew/addFl",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new XMLHttpRequest();
                //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                xhr.upload.addEventListener('progress', function (e) {
                    //loaded代表上传了多少
                    //total代表总数为多少
                    var progressRate = (e.loaded / e.total) * 100 + '%';
                    //通过设置进度条的宽度达到效果
                    $('.progress > div').css('width', progressRate);
                    $("#FlStatus").html('导入中');
                });
                return xhr;
            },
            success: function (data) {
                var msg = data.msg;
                if (msg === "导入成功") {
                    $("#FlStatus").css('color', "green");
                    self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                        location.reload(true);   // 这个方法是刷新当前页面
                    }, 2000)
                }
                $("#FlStatus").html(msg);
            }
        });
    });

    $('#deleteAllSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteAll",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteAllStatus").css('color', "green");
                $("#deleteAllStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteAirSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteAir",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteAirStatus").css('color', "green");
                $("#deleteAirStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteLastMpSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteLastMp",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteLastMpStatus").css('color', "green");
                $("#deleteLastMpStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteMpSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteMp",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteMpStatus").css('color', "green");
                $("#deleteMpStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteNextMpSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteNextMp",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteNextMpStatus").css('color', "green");
                $("#deleteNextMpStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteLastAirSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteLastAir",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteLastAirStatus").css('color', "green");
                $("#deleteLastAirStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteNextAirSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteNextAir",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteNextAirStatus").css('color', "green");
                $("#deleteNextAirStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    $('#deleteFlSubmit').click(function () {
        $.ajax({
            type: "post",
            url: "/aircrew/deleteFl",
            processData: false,
            contentType: false,
            success: function (data) {
                var msg = data.msg;
                if (msg === "清空成功")
                    $("#deleteFlStatus").css('color', "green");
                $("#deleteFlStatus").html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        });
    });

    //干部
    $('#addCadreSubmit').click(function () {
        var eid = document.getElementById("eid").value;
        var name = document.getElementById("name").value;
        var department = document.getElementById("department").value;
        var qualify = document.getElementById("qualify").value;
        var category = document.getElementById("category").value;
        $.ajax({
            type: "post",
            url: "/aircrew/addCadre",
            data: {
                eid: eid,
                name: name,
                department: department,
                qualify: qualify,
                category: category
            },
            dataType: "json",
            success: function (data) {
                var msg = data.msg;
                if (msg === "添加成功")
                    $('#cadreStatus').css('color', 'green');
                else
                    $('#cadreStatus').css('color', 'red');
                $('#cadreStatus').html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        })
    });

    //双组航线
    $('#addAirlineSubmit').click(function () {
        var airline = document.getElementById("airline").value;
        var airlineRemark = document.getElementById("airlineRemark").value;
        $.ajax({
            type: "post",
            url: "/aircrew/addAirline",
            data: {
                airline: airline,
                airlineRemark: airlineRemark
            },
            dataType: "json",
            success: function (data) {
                var msg = data.msg;
                if (msg === "添加成功")
                    $('#airlineStatus').css('color', 'green');
                else
                    $('#airlineStatus').css('color', 'red');
                $('#airlineStatus').html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        })
    });

    //过夜航班
    $('#addNightFlightSubmit').click(function () {
        var number = document.getElementById("number").value;
        var nightFlightRemarks = document.getElementById("nightFlightRemarks").value;
        $.ajax({
            type: "post",
            url: "/aircrew/addNightFlight",
            data: {
                number: number,
                nightFlightRemarks: nightFlightRemarks
            },
            dataType: "json",
            success: function (data) {
                var msg = data.msg;
                if (msg === "添加成功")
                    $('#nightFlightStatus').css('color', 'green');
                else
                    $('#nightFlightStatus').css('color', 'red');
                $('#nightFlightStatus').html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        })
    });

    //阶段双组航班
    $('#addStageDoubleFlightSubmit').click(function () {
        var number = document.getElementById("stageDoubleFlightNumber").value;
        var remarks = document.getElementById("stageDoubleFlightRemarks").value;
        $.ajax({
            type: "post",
            url: "/aircrew/addStageDoubleFlight",
            data: {
                number: number,
                remarks: remarks
            },
            dataType: "json",
            success: function (data) {
                var msg = data.msg;
                if (msg === "添加成功")
                    $('#stageDoubleFlightStatus').css('color', 'green');
                else
                    $('#stageDoubleFlightStatus').css('color', 'red');
                $('#stageDoubleFlightStatus').html(msg);
                self.setInterval(function () {  // 这个方法是说在延迟两秒后执行大括号里的方法
                    location.reload(true);   // 这个方法是刷新当前页面
                }, 2000)
            }
        })
    });
});

function eidFunction() {
    document.getElementById("cadreStatus").innerHTML = "";
    document.getElementById("airlineStatus").innerHTML = "";
    document.getElementById("nightFlightStatus").innerHTML = "";
    document.getElementById("stageDoubleFlightStatus").innerHTML = "";
}

