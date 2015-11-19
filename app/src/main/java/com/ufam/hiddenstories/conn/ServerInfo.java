package com.ufam.hiddenstories.conn;

/**
 * Created by rli on 15/06/2015.
 */
public class ServerInfo {

    public static final String fileImageExt = "jpg";
    public static final String LOGIN_ADM_FACEBOOK = "facebook-adm@qg.com";
    public static final String PASSWD_ADM_FACEBOOK = "jfhgjtu685464bdtdbVGA";

    public static final String separator = ";~;";
    //public static final String dnsNameServer = "http://10.0.3.2"; //endereço local para desenvolvimento
    public static final String dnsNameServer = "http://ec2-54-233-85-160.sa-east-1.compute.amazonaws.com"; //servidor aws

    //IMAGE FOLDER
    //public static final String imageFolder = "http://192.168.56.1/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://200.222.36.121/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://quebragalho.esy.es/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://10.0.3.2/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://192.168.1.133/quebragalho/images/__w-200-400-600-800-1000__/";
    //AWS
    //public static final String imageFolder = "http://ec2-54-233-85-160.sa-east-1.compute.amazonaws.com/quebragalho/images/__w-200-400-600-800-1000__/";
    public static final String imageFolder = dnsNameServer+"/historyapp/web/images/__w-200-400-600-800-1000__/";

}
