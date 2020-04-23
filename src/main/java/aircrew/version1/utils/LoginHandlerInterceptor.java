package aircrew.version1.utils;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jiangzhendong
 * @Description 该类用于登陆拦截控制和下载文件异常捕捉
 *
 */
@Controller
public class LoginHandlerInterceptor implements HandlerInterceptor {

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws ServletException, IOException {
//        Object user = request.getSession().getAttribute("userName");
//        if(user == null){
//            request.setAttribute("message","没有权限请先登陆");
//            request.getRequestDispatcher("/login.html").forward(request,response);
//            return false;
//        }else{
//            return true;
//        }
//    }

    @ExceptionHandler(value = FileUploadBase.FileSizeLimitExceededException.class)
    public boolean fileUploadExceptionHandler(FileUploadBase.FileSizeLimitExceededException excption, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(excption.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException){
            request.setAttribute("message","上传文件过大不得超过100M");
            request.getRequestDispatcher("pilot/addupload").forward(request,response);
        }else {
            request.setAttribute("message","服务器异常") ;
            request.getRequestDispatcher("pilot/addupload").forward(request,response);
            return false;
        }
        return true;
    }
}
