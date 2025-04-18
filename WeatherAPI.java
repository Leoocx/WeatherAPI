
import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class WeatherAPI {


    public static String getDadosClimaticos(String cidade) throws Exception{
        String apiKey = Files.readString(Paths.get("api-key.txt")).trim(); //Pego a classe Files, leio a string e passo o caminho arquivo para a classe files ler as string dentro dele e caso tenha um espaço em branco no começo e fim o trim remove
        String formatter = URLEncoder.encode(cidade, StandardCharsets.UTF_8); //Formatar a URL que o usuário digitou(evita erros de entradas pelo usuario ao digitar a cidade)
        String apiURL = "http://api.weatherapi.com/v1/current.json?key="+ apiKey + "&q=" +formatter;

        HttpRequest request = HttpRequest.newBuilder()   //COMEÇA A CONSTRUÇÃO DE UMA NOVA SOLICITAÇÃO HTTP
                .uri(URI.create(apiURL))                 //ESTE MÉTODO DEFINE O URI DA SOLICITAÇÃO HTTP
                .build();                  //FINALIZA A CONSTRUÇÃO DA SOLICITAÇÃO HTTP

        // CRIAR OBJETOS PARA ENVIAR SOLICITAÇÕES HTTP E RECEBER RESPOSTAS HTTP, PARA ACESSAR O SITE DA WEATHERAPI
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //ELE DIZ AO CLIENT COMO LIDAR COM A RESPOSTA, NESSE CASO ELE ESTÁ CONFIGURADO PARA TRATAR O CORPO DA RESPOSTA COMO UMA STRING

        return response.body();  //RETORNA OS DADOS METEREOLOGICOS OBTIDOS DA API(WEATHERAPI)
    }



    public static void exibirDados(String dados){
        System.out.println("DADOS JSON OBTIDOS NO SITE METEREOLÓGICO: "+ dados);
        JSONObject dadosJson = new JSONObject(dados);
        JSONObject infoMeteorologicas = dadosJson.getJSONObject("current");  // current SIGNIFICA QUE QUERO OS DADOS EM TEMPO REAL

        //EXTRAIR OS DADOS DA LOCALIZAÇÃO
        String cidade = dadosJson.getJSONObject("location").getString("name");
        String pais = dadosJson.getJSONObject("location").getString("country");

        //EXTRAIR DADOS ADICIONAIS

        String condicaoTempo = infoMeteorologicas.getJSONObject("condition").getString("text");
        int umidade = infoMeteorologicas.getInt("humidity");
        float velocidadeVento = infoMeteorologicas.getFloat("wind_kph");
        float pressaoAtmosferica = infoMeteorologicas.getFloat("pressure_mb");
        float sensacaoTermica = infoMeteorologicas.getFloat("feelslike_c");
        float temperaturaAtual = infoMeteorologicas.getFloat("temp_c");

        //EXTRAI A DATA E HORA DA STRING RETORNADA PELA API
        String dataHoraString = infoMeteorologicas.getString("last_updated");

        System.out.println("INFORMAÇÕES METEOROLÓGICAS PARA "+ cidade +", "+ pais);
        System.out.println("DATA: "+dataHoraString);
        System.out.println("TEMPERATURA ATUAL: "+temperaturaAtual);
        System.out.println("SENSAÇÃO TÉRMICA: "+sensacaoTermica);
        System.out.println("CONDIÇÃO CLIMÁTICA: "+condicaoTempo);
        System.out.println("UMIDADE: "+umidade);
        System.out.println("VELOCIDADE DO VENTO: "+velocidadeVento);
        System.out.println("PRESSÃO ATMOSFÉRICA: "+pressaoAtmosferica);
    }



    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.print("NOME DA CIDADE: ");
        String cidade = reader.nextLine();

        try{
            String dadosClimaticos = getDadosClimaticos(cidade); //retorna um JSON

            //código 1006 significa que os dados para a cidade não foram encontrados
            if (dadosClimaticos.contains("\"code\":1006")){   // "code":1006
                System.out.println("Cidade: "+ cidade + " não encontrada");
            }
            else{
                exibirDados(dadosClimaticos);
            }
        } catch (Exception e){
            System.out.println("ERRO: "+ e.getMessage());
        }
    }
}