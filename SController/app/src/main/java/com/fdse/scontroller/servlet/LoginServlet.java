package com.fdse.scontroller.servlet;

/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/08/10
 *     desc   :抱歉，我暂时并不想用这些BOl来进行交互
 *     version: 1.0
 * </pre>
 */
public class LoginServlet {

    public static class RequestBO{
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ResponseBO{


    }

}
