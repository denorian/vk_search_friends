package common;

import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class SearchInVK {
    public static HashMap<Integer, Users> usersHashMap = new HashMap<>();
    public static String access_token  = "c725d1c899ebd6f32e07df1ab5018e1c23b85488bfd9e3bc8f5385aeb20bf15ac0b43163c12188ab19950";
    public static int minTimeRequest = 333;

    public static void main(String[] args) throws JSONException, InterruptedException {
        LinkedHashMap<Integer, Integer> usersHashMapInteger = Users.getMatchUsers();

        System.out.println(usersHashMapInteger.size());
        StringBuilder ids = new StringBuilder();

        ArrayList<String> listIds = new ArrayList<>();
        int count = 1;
        for (Integer id : usersHashMapInteger.keySet()) {
            ids.append(id + ",");
            if(count == 375){
                listIds.add(ids.toString());
                count = 0;
                ids = new StringBuilder();
            }
            count++;
        }
        listIds.add(ids.toString());


        for (String listId : listIds) {
            String usersJSON = queryVK("users.get?user_ids=" + listId + "&fields=sex,bdate,city,status,occupation,nickname,relatives,relation");

            JSONObject jsonObjectUsers = new JSONObject();
            try {
                jsonObjectUsers = new JSONObject(usersJSON);
            }catch (Exception e){
                System.out.println(usersJSON);
            }

            JSONArray jsonArrayUsers = jsonObjectUsers.getJSONArray("response");
            System.gc();
            for (int i = 0; i < jsonArrayUsers.length(); i++) {
                int city = 0;
                int relation = 0;
                String bdate = "";
                String deactivated = "";
                JSONObject jsonObjectUser = jsonArrayUsers.getJSONObject(i);
                try {
                    city = jsonObjectUser.getInt("city");
                }catch (Exception e){}
                try {
                    relation = jsonObjectUser.getInt("relation");
                }catch (Exception e){}
                try {
                    bdate = jsonObjectUser.getString("bdate");
                }catch (Exception e){}
                try {
                    deactivated = jsonObjectUser.getString("deactivated");
                }catch (Exception e){}

                System.out.println(jsonObjectUser.getString("first_name"));
                Users user = new Users(
                        jsonObjectUser.getString("first_name"),
                        jsonObjectUser.getString("last_name"),
                        jsonObjectUser.getInt("uid"),
                        city,
                        jsonObjectUser.getInt("sex") == 2 ? true : false,
                        bdate,
                        deactivated,
                        relation
                    );

                user = null;
            }
        }

        System.exit(0);

/*
        String jsonVK = queryVK("groups.get?extended=1&user_id=806588");
        System.out.println(jsonVK);
        Groups groups = new Groups(new JSONObject(jsonVK));
        try {
            for (Integer idGroup : groups.arrayList) {
                System.gc();
                if(Groups.isCompleteGroup(idGroup))
                    continue;

                int offset = Groups.getOffsetGroup(idGroup);
                while (true){
                    ArrayList<Integer> uids = new ArrayList<>();

                    long startTime = System.currentTimeMillis();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("groups.getMembers");
                    stringBuilder.append("?group_id=" + idGroup);
                    stringBuilder.append("&offset=" + offset);
                    stringBuilder.append("&count=1000");
                    String groupMembersJSON = queryVK(stringBuilder.toString());


                    if(groupMembersJSON.equals("Too many requests per second")){
                        System.out.println(groupMembersJSON);
                        Thread.sleep(750);
                        continue;
                    }

                    System.out.println("Выполнился запрос " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                    JSONObject membersJson = new JSONObject(groupMembersJSON);
                    JSONArray members = membersJson.getJSONObject("response").getJSONArray("users");
                    if(members.length() == 0){
                        break;
                    }

                    for (int i = 0; i < members.length(); i++) {
                        uids.add(members.getInt(i));
                    }
                    Users.addGroupUserArray(uids,idGroup);
                    Groups.updateOffsetGroup(idGroup,offset);
                    System.out.println("добавили в базу " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                    int totalCountMembers = membersJson.getJSONObject("response").getInt("count");
                    if(offset < totalCountMembers){
                        offset += 1000;
                    }else{
                        break;
                    }

                    if((System.currentTimeMillis() - startTime) < minTimeRequest){
                        Thread.sleep(minTimeRequest - (System.currentTimeMillis() - startTime));
                    }

                    System.out.println("программа выполнялась " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                }
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
        */
    }

    public static String queryVK(String queryMethod){
        String response = "";
        String query = "https://api.vk.com/method/" + queryMethod + "&access_token=" + SearchInVK.access_token;
        System.out.println(query);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(query).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(0);
            connection.connect();

            StringBuilder stringBuilder = new StringBuilder();
            if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                response = stringBuilder.toString();
                reader.close();
            }else {
                return Integer.toString(connection.getResponseCode());
            }

        }catch (Exception cause){
            cause.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }

            connection = null;
        }
        try {
            JSONObject obj = new JSONObject(response);
            if(obj.getJSONObject("error").getInt("error_code") == 5){
                System.out.println("Access_token has expired");
                System.exit(1);
            }

            if(obj.getJSONObject("error").getInt("error_code") == 6){
                return "Too many requests per second";
            }
        }catch (Exception e){}

        return response;
    }
}
