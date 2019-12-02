using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Threading.Tasks;
using WeatherApp;
using WeatherApp.Models;
using WeatherXamarim.Models;
using Xamarin.Forms;

namespace WeatherXamarim
{
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
            this.BindingContext = this;
            GetServerInformation("porto", "weather");
            GetServerInformation("porto", "forecast");

        }

        public void GetServerInformation(string city, string type)
        {
            Debug.WriteLine("aqui"); // Call the Result
            Task<string> result = NetworkCheck.GetJSON(city, type);
            if (result == null)
            {
                DisplayAlert("JSONParsing", "No network is available.", "Ok");
                Debug.WriteLine("No internet! Avisar isso");
                return;
            }
            Debug.WriteLine("result:: " + result.Result); // Call the Result
            var json = result.Result;
            //everything okay
            if (json != "")
            {
                if (type.Equals("weather"))
                    BindWeatherInformation(JsonConvert.DeserializeObject<RootObjectWeather>(json));
                else if (type.Equals("forecast"))
                    BindForecastInformation(JsonConvert.DeserializeObject<RootObjectForecast>(json));
                else
                    Debug.WriteLine("weather type", "algo merdou");
            }
            else
                DisplayAlert("JSONParsing", "Something went wrong", "Ok");

        }
        private void BindWeatherInformation(RootObjectWeather root)
        {
            CurrentTemperature.Text = root.main.temp.ToString();
            City.Text = root.name + ", " + root.sys.country;
            Description.Text = Utils.FirstCharToUpper(root.weather[0].description);
            // TODO: ver isto
            if (root.rain != null ) {
                Debug.WriteLine("itgonrain", root.rain.__invalid_name__1h.ToString());
                if (root.rain.__invalid_name__1h > 0)
                    Description.Text += " (" + root.rain.__invalid_name__1h.ToString() + " mm)";
            }
            Humidity.Text = root.main.humidity.ToString() + "%";
            Pressure.Text = root.main.pressure.ToString() + " hpa";
            Cloudiness.Text = root.clouds.all.ToString() + "%";
            Wind.Text = root.wind.speed.ToString() + " m/s";
            Date.Text = DateTime.Today.Date.ToString("dd/MM/yyyy");
        }

        private void BindForecastInformation(RootObjectForecast root)
        {
            root.list.RemoveRange(8, root.list.Count - 8);

            WeatherForecastList.ItemsSource = root.list;
        }


        private async void WeatherForecastList_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            List info = (List) e.Item;
            await Navigation.PushAsync(new DetailForecastPage(info));

        }


        public List<Weather> Weathers { get => WeatherData(); }

        private List<Weather> WeatherData()
        {
            var tempList = new List<Weather>();
            tempList.Add(new Weather { Temp = "22", Date = "Sunday 16", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "21", Date = "Monday 17", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "20", Date = "Tuesday 18", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "12", Date = "Wednesday 19", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "17", Date = "Thursday 20", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "20", Date = "Friday 21", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "22", Date = "Sunday 16", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "21", Date = "Monday 17", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "20", Date = "Tuesday 18", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "12", Date = "Wednesday 19", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "17", Date = "Thursday 20", Icon = "weather.png" });
            tempList.Add(new Weather { Temp = "20", Date = "Friday 21", Icon = "weather.png" });

            return tempList;
        }

    }




    public class Weather
    {
        public string Temp { get; set; }
        public string Date { get; set; }
        public string Icon { get; set; }
    }
}
