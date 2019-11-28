using Plugin.Connectivity;
using System;
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

                var client = new System.Net.Http.HttpClient();
                var response = await client.GetAsync(urlAuthority + " weather?q=porto,portugal&units=metric&appid=" + appid);
                string json = await response.Content.ReadAsStringAsync();
                return json;
               
            }
            else
            {
                //await DisplayAlert("JSONParsing", "No network is available.", "Ok");
            }
            return "";
            //Hide loader after server response    
            // ProgressLoader.IsVisible = false;
        }
    }
}
