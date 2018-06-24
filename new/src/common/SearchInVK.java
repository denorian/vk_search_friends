package common;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SearchInVK {
    public static HashMap<Integer, Users> usersHashMap = new HashMap<>();
    public static String access_token = "892add35419e0d1ce26dd1ad95318d16a7978dc657965529c85dd7967624e02d776f5e931807e937f782d";
    public static int minTimeRequest = 500;
    public static long timeRequest = 0;

    public static void main(String[] args) throws JSONException, InterruptedException {

        // получим группы своего пользователя
        //SearchInVK.getGroups();
        //SearchInVK.getUsersGroup();
        //SearchInVK.getUsersGroupV2();
        //SearchInVK.searchGirls();
        SearchInVK.searchGirlsV2();


        System.exit(0);
    }

    private static void searchGirls() throws JSONException, InterruptedException {
        LinkedHashMap<Integer, Integer> usersHashMapInteger = Users.getMatchUsers();

        System.out.println(usersHashMapInteger.size());
        StringBuilder ids = new StringBuilder();

        ArrayList<String> listIds = new ArrayList<>();
        int count = 1;
        for (Integer id : usersHashMapInteger.keySet()) {
            ids.append(id + ",");
            if (count == 250) {
                listIds.add(ids.toString());
                count = 0;
                ids = new StringBuilder();
            }
            count++;
        }
        listIds.add(ids.toString());


        for (String listId : listIds) {
            String usersJSON = queryVK("users.get?user_ids=" + listId + "&fields=sex,bdate,city,occupation,nickname,relation,counters,last_seen&v=5.78");
            Thread.sleep(250);
            JSONObject jsonObjectUsers = new JSONObject();
            try {
                jsonObjectUsers = new JSONObject(usersJSON);
            } catch (Exception e) {
                System.out.println(usersJSON);
            }
            JSONArray jsonArrayUsers = jsonObjectUsers.getJSONArray("response");

            for (int i = 0; i < jsonArrayUsers.length(); i++) {
                int city = 0;
                int relation = 0;
                long lastSeen = 0;
                String bdate = "";
                String deactivated = "";
                JSONObject jsonObjectUser = jsonArrayUsers.getJSONObject(i);
                System.out.println(jsonObjectUser.toString());

                try {
                    deactivated = jsonObjectUser.getString("deactivated");
                    if (deactivated.equals("banned"))
                        continue;
                    if (deactivated.equals("deleted"))
                        continue;
                } catch (Exception e) {
                }
                /*try {
                    JSONObject counters = jsonObjectUser.getJSONObject("counters");
                    try {
                        if(counters.getInt("friends") > 1500)
                            continue;
                    }catch (Exception e){}
                    try {
                        if(counters.getInt("groups") > 750)
                            continue;
                    }catch (Exception e){}
                    try {
                        if(counters.getInt("followers") > 3000)
                            continue;
                    }catch (Exception e){}
                }catch (Exception e){}*/
                try {
                    city = jsonObjectUser.getJSONObject("city").getInt("id");
                } catch (Exception e) {
                }
                try {
                    relation = jsonObjectUser.getInt("relation");
                } catch (Exception e) {
                }
                try {
                    bdate = jsonObjectUser.getString("bdate");
                } catch (Exception e) {
                }
                try {
                    JSONObject lastSeenObject = jsonObjectUser.getJSONObject("last_seen");
                    lastSeen = lastSeenObject.getLong("time");
                    long unixTime = System.currentTimeMillis() / 1000L;
                    if ((unixTime - lastSeen) > (86400 * 30)) {
                        continue;
                    }
                } catch (Exception e) {
                }

                Users user = new Users(
                        jsonObjectUser.getString("first_name"),
                        jsonObjectUser.getString("last_name"),
                        jsonObjectUser.getInt("id"),
                        city,
                        false,
                        bdate,
                        deactivated,
                        relation
                );

                user = null;
            }
        }
    }
    private static void searchGirlsV2() throws JSONException, InterruptedException {
        LinkedHashMap<Integer, Integer> usersHashMapInteger = Users.getMatchUsers();

        System.out.println(usersHashMapInteger.size());
        StringBuilder ids = new StringBuilder();

        ArrayList<String> listIds = new ArrayList<>();
        int count = 1;
        for (Integer id : usersHashMapInteger.keySet()) {
            ids.append(id + ",");
            if (count == 1) {
                listIds.add(ids.toString());
                count = 0;
                ids = new StringBuilder();
            }
            count++;
        }
        listIds.add(ids.toString());


        for (String listId : listIds) {
            String usersJSON = queryVK("users.get?user_ids=" + listId + "&fields=sex,bdate,city,occupation,nickname,relation,counters,last_seen&v=5.78");
            Thread.sleep(250);
            JSONObject jsonObjectUsers = new JSONObject();
            try {
                jsonObjectUsers = new JSONObject(usersJSON);
            } catch (Exception e) {
                System.out.println(usersJSON);
            }
            JSONArray jsonArrayUsers = jsonObjectUsers.getJSONArray("response");

            for (int i = 0; i < jsonArrayUsers.length(); i++) {
                int city = 0;
                int relation = 0;
                long lastSeen = 0;
                String bdate = "";
                String deactivated = "";
                JSONObject jsonObjectUser = jsonArrayUsers.getJSONObject(i);
                System.out.println(jsonObjectUser.toString());

                try {
                    deactivated = jsonObjectUser.getString("deactivated");
                    if (deactivated.equals("banned"))
                        continue;
                    if (deactivated.equals("deleted"))
                        continue;
                } catch (Exception e) {
                }
                try {
                    JSONObject counters = jsonObjectUser.getJSONObject("counters");
                    try {
                        if(counters.getInt("friends") > 1500)
                            continue;
                    }catch (Exception e){}
                    try {
                        if(counters.getInt("groups") > 750)
                            continue;
                    }catch (Exception e){}
                    try {
                        if(counters.getInt("followers") > 2000)
                            continue;
                    }catch (Exception e){}
                }catch (Exception e){}
                try {
                    city = jsonObjectUser.getJSONObject("city").getInt("id");
                } catch (Exception e) {
                }
                try {
                    relation = jsonObjectUser.getInt("relation");
                } catch (Exception e) {
                }
                try {
                    bdate = jsonObjectUser.getString("bdate");
                } catch (Exception e) {
                }
                try {
                    JSONObject lastSeenObject = jsonObjectUser.getJSONObject("last_seen");
                    lastSeen = lastSeenObject.getLong("time");
                    long unixTime = System.currentTimeMillis() / 1000L;
                    if ((unixTime - lastSeen) > (86400 * 30)) {
                        continue;
                    }
                } catch (Exception e) {
                }

                Users user = new Users(
                        jsonObjectUser.getString("first_name"),
                        jsonObjectUser.getString("last_name"),
                        jsonObjectUser.getInt("id"),
                        city,
                        false,
                        bdate,
                        deactivated,
                        relation
                );

                user = null;
            }
        }
    }

    private static void getUsersGroup() throws JSONException {
        Groups groups = new Groups();
        try {
            for (Integer idGroup : groups.arrayList) {
                if (Groups.isCompleteGroup(idGroup))
                    continue;

                int offset = Groups.getOffsetGroup(idGroup);
                while (true) {
                    ArrayList<Integer> uids = new ArrayList<>();

                    long startTime = System.currentTimeMillis();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("groups.getMembers");
                    stringBuilder.append("?group_id=" + idGroup);
                    stringBuilder.append("&offset=" + offset);
                    stringBuilder.append("&v=5.78");
                    stringBuilder.append("&count=1000");
                    stringBuilder.append("&fields=sex,bdate,city,relation");
                    String groupMembersJSON = queryVK(stringBuilder.toString());

                    if (groupMembersJSON.equals("Too many requests per second")) {
                        System.out.println(groupMembersJSON);
                        Thread.sleep(750);
                        continue;
                    }

                    System.out.println("Выполнился запрос " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                    JSONObject membersJson = new JSONObject(groupMembersJSON);
                    JSONArray members = membersJson.getJSONObject("response").getJSONArray("items");
                    if (members.length() == 0) {
                        break;
                    }

                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        if (member.getInt("sex") == 2)
                            continue;
                        try {
                            int city_id = member.getJSONObject("city").getInt("id");
                            if (city_id != 1 && city_id != 72)
                                continue;
                        } catch (Exception e) {
                            continue;
                        }

                        uids.add(member.getInt("id"));
                    }

                    if (uids.size() > 0) {
                        Users.addGroupUserArray(uids, idGroup);
                    }
                    Groups.updateOffsetGroup(idGroup, offset);
                    System.out.println("добавили в базу " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                    int totalCountMembers = membersJson.getJSONObject("response").getInt("count");
                    if (offset < totalCountMembers) {
                        offset += 1000;
                    } else {
                        Groups.updateOffsetGroup(idGroup, totalCountMembers);
                        break;
                    }

                    if ((System.currentTimeMillis() - startTime) < minTimeRequest) {
                        Thread.sleep(minTimeRequest - (System.currentTimeMillis() - startTime));
                    }

                    System.out.println("программа выполнялась " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void getUsersGroupV2() throws JSONException {
        Groups groups = new Groups();
        try {
            for (Integer idGroup : groups.arrayList) {
                if (Groups.isCompleteGroup(idGroup))
                    continue;

                    ArrayList<Integer> uids = new ArrayList<>();

                    long startTime = System.currentTimeMillis();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("users.search");
                    stringBuilder.append("?count=1000");
                    stringBuilder.append("&city=72");
                    stringBuilder.append("&country=1");
                    stringBuilder.append("&sex=1");
                    stringBuilder.append("&age_from=22");
                    stringBuilder.append("&age_to=29");
                    stringBuilder.append("&status=1");
                    stringBuilder.append("&group_id=" + idGroup);
                    stringBuilder.append("&v=5.8");
                    String groupMembersJSON = queryVK(stringBuilder.toString());
                    System.out.println(groupMembersJSON);

                    if (groupMembersJSON.equals("Too many requests per second")) {
                        System.out.println(groupMembersJSON);
                        Thread.sleep(750);
                        continue;
                    }

                    System.out.println("Выполнился запрос " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                    JSONObject membersJson = new JSONObject(groupMembersJSON);
                    JSONArray members = membersJson.getJSONObject("response").getJSONArray("items");
                    int totalCountMembers = membersJson.getJSONObject("response").getInt("count");
                    System.out.println("totalCountMembers" + totalCountMembers);
                    Groups.updateOffsetGroup(idGroup, totalCountMembers);
                    if (members.length() == 0) {
                        continue;
                    }

                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        uids.add(member.getInt("id"));
                    }

                    if (uids.size() > 0) {
                        Users.addGroupUserArray(uids, idGroup);
                    }

                    System.out.println("цикл выполнялся " + (System.currentTimeMillis() - startTime) + " миллисекунд");
                }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static Groups getGroups() throws JSONException, InterruptedException {
        String jsonVK = queryVK("groups.get?extended=1&user_id=806588&v=5.78");
        return new Groups(new JSONObject(jsonVK));
    }

    public static String queryVK(String queryMethod) throws InterruptedException {
        long startTime = SearchInVK.timeRequest;
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
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                response = stringBuilder.toString();
                reader.close();
            } else {
                return Integer.toString(connection.getResponseCode());
            }

        } catch (Exception cause) {
            cause.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getJSONObject("error").getInt("error_code") == 5) {
                System.out.println("Access_token has expired");
                System.exit(1);
            }

            if (obj.getJSONObject("error").getInt("error_code") == 6) {
                return "Too many requests per second";
            }
        } catch (Exception e) {
        }

        if ((System.currentTimeMillis() - startTime) < minTimeRequest) {
            Thread.sleep(minTimeRequest - (System.currentTimeMillis() - startTime));
        }
        SearchInVK.timeRequest = System.currentTimeMillis();
        return response;
    }
}
