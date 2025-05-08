package metier;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonSimpleExample {
    public static void main(String[] args) {
        // Créer un objet JSON
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nom", "John");
        jsonObject.put("age", 30);
        jsonObject.put("ville", "Paris");

        // Convertir un objet Java en chaîne JSON
        String jsonString = jsonObject.toJSONString();
        System.out.println("JSON String : " + jsonString);

        // Parseur pour convertir la chaîne JSON en objet
        JSONParser parser = new JSONParser();
        try {
            JSONObject parsedObject = (JSONObject) parser.parse(jsonString);
            System.out.println("Nom : " + parsedObject.get("nom"));
            System.out.println("Âge : " + parsedObject.get("age"));
            System.out.println("Ville : " + parsedObject.get("ville"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
