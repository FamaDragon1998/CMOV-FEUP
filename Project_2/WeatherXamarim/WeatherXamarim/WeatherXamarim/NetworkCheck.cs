using Plugin.Connectivity;
using System;
using System.Diagnostics;
using System.Threading.Tasks;

namespace WeatherApp
{
    class NetworkCheck
    {
       public static String appid = "47e4a3961fc66adef709746b5627c5d5";
       public static String urlAuthority = "https://api.openweathermap.org/data/2.5/";

        public static bool IsInternet()
        {
            if (CrossConnectivity.Current.IsConnected)
            {
                return true;
            }
            else
            {
                return false;
            }
        }


        public static async Task<string> GetJSON(string city, string type)
        {
            //Check network status   
            if (NetworkCheck.IsInternet())
            {
                Debug.WriteLine("has internet");

                var client = new System.Net.Http.HttpClient();
                var url = urlAuthority + type + "?q="+city+",pt&units=metric&appid=" + appid;
                Debug.WriteLine("url", url);
                var response = await client.GetAsync(url).ConfigureAwait(false);
                response.EnsureSuccessStatusCode();

                string json = await response.Content.ReadAsStringAsync().ConfigureAwait(false);
                Debug.WriteLine("ParsingJson", json);

                return json;
               
            }
            else
            {
                return null;
            }
        }
       
    }
}
