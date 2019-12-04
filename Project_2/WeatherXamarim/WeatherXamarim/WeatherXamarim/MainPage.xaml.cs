using Microcharts;
using Newtonsoft.Json;
using SkiaSharp;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Net.Http.Headers;
using System.Net.Http;
using System.Threading.Tasks;
using WeatherApp;
using WeatherApp.Models;
using WeatherXamarim.Models;
using Xamarin.Forms;
using Entry = Microcharts.Entry;
using Newtonsoft.Json.Linq;

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
            Title = "Porto" + ", Portugal";
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
                    BindForecastInformationAsync(JsonConvert.DeserializeObject<RootObjectForecast>(json));
                else
                    Debug.WriteLine("weather type", "algo merdou");
            }
            else
                DisplayAlert("JSONParsing", "Something went wrong", "Ok");

        }
        private void BindWeatherInformation(RootObjectWeather root)
        {
            CurrentTemperature.Text = root.main.temp.ToString();
            Description.Text = Utils.FirstCharToUpper(root.weather[0].description);
            SetBackgroundImage(root.weather[0].description);
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

        private void SetBackgroundImage(string description)
        {
            switch (description)
            {
                case "clear sky":
                    WeatherBackground.Source = "sunny.png";
                    break;
                case "few clouds":
                    WeatherBackground.Source = "slightlycloudy.png";
                    break;
                case "scattered clouds":
                    WeatherBackground.Source = "slightlycloudy.png";
                    break;
                case "broken clouds":
                    WeatherBackground.Source = "cloudy.png";
                    break;
                case "shower rain":
                    WeatherBackground.Source = "rainy.png";
                    break;
                case "rain":
                    WeatherBackground.Source = "rainy.png";
                    break;
                case "thunderstorm":
                    WeatherBackground.Source = "zeushatesyou.png";
                    break;
                case "mist":
                    WeatherBackground.Source = "mist.png";
                    break;
                case "snow":
                    WeatherBackground.Source = "snow.png";
                    break;
                default:
                    WeatherBackground.Source = "overlay.png";
                    break;
            }
        }

        private async Task BindForecastInformationAsync(RootObjectForecast root)
        {
            root.list.RemoveRange(8, root.list.Count - 8);

            WeatherForecastList.ItemsSource = root.list;

            List<Entry> entries = new List<Entry>();

            foreach (var element in root.list)
            {
                float temperature = (float) element.main.temp;

                var entry = new Entry(temperature)
                {
                    Color = SKColor.Parse("#FF1493"),
                    Label = element.Date,
                    ValueLabel = temperature.ToString() + " ºC"
                };
                entries.Add(entry);
            }

            ForecastChart.Chart = new LineChart { 
                Entries = entries,
                LineMode = LineMode.Straight,  
                LabelTextSize = 30f,
            };
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

        private void ToggleChartOrList_Clicked(object sender, EventArgs e)
        {
            WeatherForecastChart.IsVisible = !WeatherForecastChart.IsVisible;
            WeatherForecastList.IsVisible = !WeatherForecastList.IsVisible;
        }
    }




    public class Weather
    {
        public string Temp { get; set; }
        public string Date { get; set; }
        public string Icon { get; set; }
    }
}
