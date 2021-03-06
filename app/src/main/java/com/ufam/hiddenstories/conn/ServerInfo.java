package com.ufam.hiddenstories.conn;

/**
 * Created by rli on 15/06/2015.
 */
public class ServerInfo {

    public static final String EXTENSION_IMAGE_FILE = "jpg";
    public static final String LOGIN_ADM_FACEBOOK = "facebook-adm@qg.com";
    public static final String PASSWD_ADM_FACEBOOK = "jfhgjtu685464bdtdbVGA";

    public static final String separator = ";~;";
    //public static final String SERVER_ADDR = "http://10.0.3.2"; //endereço local para desenvolvimento
    public static final String SERVER_ADDR = "http://ec2-54-233-85-160.sa-east-1.compute.amazonaws.com"; //servidor aws
    //public static final String SERVER_ADDR = "http://10.0.3.2/historyapp/server.php"; //servidor aws
    public static final String SERVER_FOLDER = "/historyapp/app.php"; //servidor aws

    public static final String CATEGORY_LIST = SERVER_ADDR + SERVER_FOLDER + "/category"; //servidor aws
    public static final String GET_PLACE_BY_CAT = SERVER_ADDR + SERVER_FOLDER + "/place/getbycat"; //servidor aws
    public static final String FIND_PLACE = SERVER_ADDR + SERVER_FOLDER + "/place/find";
    public static final String LOGIN = SERVER_ADDR + SERVER_FOLDER + "/user/login";
    public static final String CREATE_USER = SERVER_ADDR + SERVER_FOLDER + "/user/create";
    public static final String DELETE_USER = SERVER_ADDR + SERVER_FOLDER + "/user/delete";
    public static final String UPDATE_USER = SERVER_ADDR + SERVER_FOLDER + "/user/update";
    public static final String SET_FAVORITE = SERVER_ADDR + SERVER_FOLDER + "/favorite/mark";
    public static final String UNSET_FAVORITE = SERVER_ADDR + SERVER_FOLDER + "/favorite/unmark";
    public static final String  CHECK_FAVORITE = SERVER_ADDR + SERVER_FOLDER + "/favorite/check";
    public static final String  SET_RATING = SERVER_ADDR + SERVER_FOLDER + "/rating/set";
    public static final String  UPDATE_RATING = SERVER_ADDR + SERVER_FOLDER + "/rating/update";
    public static final String  GET_RATING = SERVER_ADDR + SERVER_FOLDER + "/rating/get";
    public static final String  GET_RATING_LIST = SERVER_ADDR + SERVER_FOLDER + "/rating/list";
    public static final String  CREATE_PLACE = SERVER_ADDR + SERVER_FOLDER + "/place/create";
    public static final String  LIST_PICTURE_BY_PLACE = SERVER_ADDR + SERVER_FOLDER + "/picture/getbyplace";
    public static final String  SEND_PICTURE = SERVER_ADDR + SERVER_FOLDER + "/picture/send";
    public static final String  GET_ALL_PLACE = SERVER_ADDR + SERVER_FOLDER + "/place/getall";
    public static final String  GET_COMMENTARY_LIST = SERVER_ADDR + SERVER_FOLDER + "/commentary/getallbyplace";
    public static final String  ACTION_SET_COMMENTARY = SERVER_ADDR + SERVER_FOLDER + "/commentary/set";
    public static final String  ACTION_DELETE_COMMENTARY = SERVER_ADDR + SERVER_FOLDER + "/commentary/delete";






    //IMAGE FOLDER
    //public static final String imageFolder = "http://192.168.56.1/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://200.222.36.121/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://quebragalho.esy.es/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://10.0.3.2/quebragalho/images/__w-200-400-600-800-1000__/";
    //public static final String imageFolder = "http://192.168.1.133/quebragalho/images/__w-200-400-600-800-1000__/";
    //AWS
    //public static final String imageFolder = "http://ec2-54-233-85-160.sa-east-1.compute.amazonaws.com/quebragalho/images/__w-200-400-600-800-1000__/";
    public static final String IMAGE_FOLDER = SERVER_ADDR+"/historyapp/web/images/__w-200-400-600-800-1000__/";

}
