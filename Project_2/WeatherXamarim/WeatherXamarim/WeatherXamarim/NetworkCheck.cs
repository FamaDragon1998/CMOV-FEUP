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
                // write your code if there is no Internet available      
                return false;
            }
        }



        public static async Task<string> GetJSON()
        {
            //Check network status   
            if (NetworkCheck.IsInternet())
            {
                Debug.WriteLine("has internet");

                var client = new System.Net.Http.HttpClient();
                var url = urlAuthority + "weather?q=porto,portugal&units=metric&appid=" + appid;
                Debug.WriteLine("url", url);
                var response = await client.GetAsync(url);
                Debug.WriteLine("response");
                Debug.WriteLine(response);

                string json = await response.Content.ReadAsStringAsync();
                Debug.WriteLine("ParsingJson", json);

                return json;
               
            }
            else
            {
                Debug.WriteLine("no internet");
                return null;

                //await DisplayAlert("JSONParsing", "No network is available.", "Ok");
            }
            //Hide loader after server response    
            // ProgressLoader.IsVisible = false;
        }
    }
}
