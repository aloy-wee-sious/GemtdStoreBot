package Connector;

import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Aloy on 4/3/2018.
 */
public class HTTPConnector {
    private static String URL = "http://101.200.189.65:430/gemtd/201803/goods/list/@76561198047094522?hehe=0.2642897393088788";
    private static Integer expire = null;
    private static HashMap<String, String> abilityMap = new HashMap<>();
    private static HashMap<String, String> herosMap = new HashMap<>();
    private static HashMap<String, String> effectsMap = new HashMap<>();

    static {
        abilityMap.put("a101", "heal");
        abilityMap.put("a102", "Evade");
        abilityMap.put("a103", "Guard");
        abilityMap.put("a105", "Revenge");
        abilityMap.put("a201", "Sapphire");
        abilityMap.put("a202", "Opal");
        abilityMap.put("a203", "Diamond");
        abilityMap.put("a204", "Ruby");
        abilityMap.put("a205", "Emerald");
        abilityMap.put("a206", "Aquamarine");
        abilityMap.put("a207", "Topaz");
        abilityMap.put("a208", "Amethyst");
        abilityMap.put("a211", "Adjacent Swap");
        abilityMap.put("a212", "StoneHenge");
        abilityMap.put("a301", "Fast Shoot");
        abilityMap.put("a302", "Crit");
        abilityMap.put("a303", "Aim");
        abilityMap.put("a304", "Hummer");
        abilityMap.put("a306", "TimeLapse");
        abilityMap.put("a307", "Fatal Bonds");
        abilityMap.put("a308", "Ursol Whirl");
        abilityMap.put("a401", "Swap");
        abilityMap.put("a210", "Common Pray");
        abilityMap.put("a305", "Flawless Pray");
        abilityMap.put("a402", "Perfect Pray");
        abilityMap.put("a403", "Candy Marker");
    }

    static {
        herosMap.put("h000", "Riki");
        herosMap.put("h101", "Enchantress");
        herosMap.put("h102", "Puck");
        herosMap.put("h103", "Omniknight");
        herosMap.put("h104", "Wisp");
        herosMap.put("h105", "Ogre Magi");
        herosMap.put("h106", "Lion");
        herosMap.put("h107", "Keeper of The Light");
        herosMap.put("h108", "Rubick");
        herosMap.put("h109", "Jakiro");
        herosMap.put("h110", "Sand King");
        herosMap.put("h201", "Crystal Maiden");
        herosMap.put("h202", "Death Prophet");
        herosMap.put("h203", "Templar Assassin");
        herosMap.put("h204", "Lina");
        herosMap.put("h205", "Tidehunter");
        herosMap.put("h206", "Naga Siren");
        herosMap.put("h207", "Phoenix");
        herosMap.put("h208", "Dazzle");
        herosMap.put("h209", "Warlock");
        herosMap.put("h210", "Necrolyte");
        herosMap.put("h211", "Lich");
        herosMap.put("h212", "Furion");
        herosMap.put("h213", "Venomancer");
        herosMap.put("h214", "Kunkka");
        herosMap.put("h215", "Axe");
        herosMap.put("h216", "Slark");
        herosMap.put("h217", "Viper");
        herosMap.put("h218", "Tusk");
        herosMap.put("h219", "Abaddon");
        herosMap.put("h301", "Windrunner");
        herosMap.put("h302", "Phantom Assassin");
        herosMap.put("h303", "Sniper");
        herosMap.put("h304", "Sven");
        herosMap.put("h305", "Luna");
        herosMap.put("h306", "Mirana");
        herosMap.put("h307", "Nevermore");
        herosMap.put("h308", "Queen of Pain");
        herosMap.put("h309", "Juggernaut");
        herosMap.put("h310", "Pudge");
        herosMap.put("h311", "Shredder");
        herosMap.put("h312", "Slardar");
        herosMap.put("h313", "Antimage");
        herosMap.put("h314", "Bristleback");
        herosMap.put("h315", "Lycan");
        herosMap.put("h316", "Lone Druid");
        herosMap.put("h401", "Vengeful Spirit");
        herosMap.put("h402", "Invoker");
        herosMap.put("h403", "Alchemist");
        herosMap.put("h404", "Spectre");
        herosMap.put("h405", "Morphling");
        herosMap.put("h406", "Techies");
        herosMap.put("h407", "Chaos Knight");
        herosMap.put("h408", "Faceless Void");
        herosMap.put("h409", "Legion Commander");
        herosMap.put("h410", "Monkey King");
        herosMap.put("h411", "Razor");
        herosMap.put("h412", "Tinker");
        herosMap.put("h413", "Pangolier");
        herosMap.put("h414", "Dark Willow");
    }

    static {
        effectsMap.put("e101", "Devine");
        effectsMap.put("e102", "Ruby");
        effectsMap.put("e103", "Fireworks");
        effectsMap.put("e104", "CrystalRift");
        effectsMap.put("e105", "Cursed");
        effectsMap.put("e106", "Poisonous");
        effectsMap.put("e107", "Dire2012");
        effectsMap.put("e108", "Dire2013");
        effectsMap.put("e109", "EarthSpirit");
        effectsMap.put("e110", "BlueStorm");
        effectsMap.put("e111", "HappyTogether");
        effectsMap.put("e112", "Gem");
        effectsMap.put("e113", "Foggy");
        effectsMap.put("e201", "Illusion");
        effectsMap.put("e202", "KingGhosts");
        effectsMap.put("e203", "Polycount");
        effectsMap.put("e204", "BaneWard");
        effectsMap.put("e205", "Fungal");
        effectsMap.put("e206", "TI2012Blue");
        effectsMap.put("e207", "TI2013Green");
        effectsMap.put("e208", "TI2014Purple");
        effectsMap.put("e209", "NeonButterfly");
        effectsMap.put("e210", "WhirlingSparks");
        effectsMap.put("e211", "Foggy");
        effectsMap.put("e212", "Fantastic");
        effectsMap.put("e301", "Highlight");
        effectsMap.put("e302", "Shagbark");
        effectsMap.put("e303", "Maiden");
        effectsMap.put("e304", "Grass");
        effectsMap.put("e305", "Blossoms");
        effectsMap.put("e306", "Winter");
        effectsMap.put("e307", "Lava");
        effectsMap.put("e308", "FishBubble");
        effectsMap.put("e309", "PurplePassion");
        effectsMap.put("e310", "Snowfall");
        effectsMap.put("e311", "VickedFlame");
        effectsMap.put("e312", "FlyingCoins");
        effectsMap.put("e313", "GloryTimes");
        effectsMap.put("e314", "PurpleMeteor");
        effectsMap.put("e315", "Nightmare");
        effectsMap.put("e316", "Stars");
        effectsMap.put("e317", "Love&Love");
        effectsMap.put("e319", "ShiningStars");
        effectsMap.put("e320", "Maelstrom");
        effectsMap.put("e401", "RoshLava");
        effectsMap.put("e402", "RoshFrost");
        effectsMap.put("e403", "RoshGold");
        effectsMap.put("e404", "RoshPlatinum");
        effectsMap.put("e405", "Brilliant");
        effectsMap.put("e406", "StarSapphire");
        effectsMap.put("e407", "Sand&Rock");
        effectsMap.put("e408", "DarkMoon");
        effectsMap.put("e409", "Bloody");
    }

    public static String getGoods() throws IOException {
        String result;
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error, Status not 200. " + connection.getResponseCode() + "\n" + connection.getResponseMessage());
        } else {
            result = getResult(connection.getInputStream());
        }

        return result;
    }

    private static String getResult(InputStream inputStream) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                    br.close();
            }
        }

        return formatResponse(sb.toString());
    }

    private static String formatResponse(String JSONString) {
        JSONObject jsonObject = new JSONObject(JSONString);
        JSONObject goods = (JSONObject) jsonObject.get("list");
        String onSales = jsonObject.getString("onsale");
        expire = jsonObject.getInt("expire");
        Iterator<String> keys = goods.keys();
        ArrayList<String> abilitiesResult = new ArrayList<>();
        ArrayList<String> herosResult = new ArrayList<>();
        ArrayList<String> effectsResult = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String id = goods.getJSONObject(key).getString("id");
            if (herosMap.containsKey(id)) {
                herosResult.add(herosMap.get(id));
            } else if (abilityMap.containsKey(id)) {
                abilitiesResult.add(abilityMap.get(id));
            } else if (effectsMap.containsKey(id)) {
                effectsResult.add(effectsMap.get(id));
            }
        }

        Collections.sort(herosResult);
        Collections.sort(effectsResult);
        Collections.sort(abilitiesResult);
        String salesName = herosMap.containsKey(onSales) ? herosMap.get(onSales) : abilityMap.containsKey(onSales) ? abilityMap.get(onSales) : effectsMap.get(onSales);

        String result = "==== Ability ====\n";
        for (String s : abilitiesResult) {
            if (s.equals(salesName)) {
                result = result + s + " (SALE!)\n";
            } else {
                result = result + s + "\n";
            }
        }
        result = result + "==== Hero ====\n";
        for (String s : herosResult) {
            if (s.equals(salesName)) {
                result = result + s + " (SALE!)\n";
            } else {
                result = result + s + "\n";
            }
        }
        result = result + "==== Effect ====\n";
        for (String s : effectsResult) {
            if (s.equals(salesName)) {
                result = result + s + " (SALE!)\n";
            } else {
                result = result + s + "\n";
            }
        }
        return result;
    }

    public static void postDiscord(String webhookURL, String username, String avatarUrl, String goods) {
        JSONObject content = new JSONObject();
        content.put("content", goods);
        content.put("username", username);
        content.put("avatar_url", avatarUrl);

        HttpRequest.post(webhookURL)
                .acceptJson()
                .contentType("application/json")
                .header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11") // Why? Because discordapp.com blocks the default User Agent
                .send(content.toString())
                .body();
    }

    public static Integer getExpire() {
        return expire;
    }
}


/*

{
   "err":0,
   "list":{
      "h0":{
         "id":"b201711",
         "price":0,
         "pic":"file://{images}/custom_game/lottery/box201803.png",
         "rarity":"luckybox"
      },
      "h218":{
         "id":"h218",
         "price":20,
         "pic":"hero_avatar",
         "rarity":"rare_hero"
      },
      "h315":{
         "id":"h315",
         "price":40,
         "pic":"hero_avatar",
         "rarity":"mythical_hero"
      },
      "h316":{
         "id":"h316",
         "price":40,
         "pic":"hero_avatar",
         "rarity":"mythical_hero"
      },
      "h108":{
         "id":"h108",
         "price":10,
         "pic":"hero_avatar",
         "rarity":"common_hero"
      },
      "h102":{
         "id":"h102",
         "price":10,
         "pic":"hero_avatar",
         "rarity":"common_hero"
      },
      "a210":{
         "id":"a210",
         "price":10,
         "pic":"file://{images}/custom_game/lottery/gem_a210.png",
         "rarity":"rare_ability"
      },
      "a203":{
         "id":"a203",
         "price":10,
         "pic":"file://{images}/custom_game/lottery/gem_a203.png",
         "rarity":"rare_ability"
      },
      "a401":{
         "id":"a401",
         "price":160,
         "pic":"file://{images}/custom_game/lottery/gem_a401.png",
         "rarity":"legendary_ability"
      },
      "a305":{
         "id":"a305",
         "price":15,
         "pic":"file://{images}/custom_game/lottery/gem_a305.png",
         "rarity":"mythical_ability"
      },
      "e108":{
         "id":"e108",
         "price":50,
         "pic":"file://{images}/custom_game/lottery/e108.png",
         "rarity":"common_effect"
      },
      "e319":{
         "id":"e319",
         "price":200,
         "pic":"file://{images}/custom_game/lottery/e319.png",
         "rarity":"mythical_effect"
      }
   },
   "expire":34993,
   "onsale":"h108"
}


 */



