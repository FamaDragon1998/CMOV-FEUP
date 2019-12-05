using Microcharts;
using Newtonsoft.Json;
using SkiaSharp;
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
        RootObjectForecast forecastRoot;

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

      
        private void BindForecastInformation(RootObjectForecast root)
        {
            root.list.RemoveRange(8, root.list.Count - 8);
            WeatherForecastList.ItemsSource = root.list;
            forecastRoot = root;

        }

        private async void WeatherForecastList_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            List info = (List) e.Item;
            await Navigation.PushAsync(new ForecastDetailPage(info));
        }

        private async void ForecastChart_clicked(object sender, EventArgs args)
        {
            await Navigation.PushAsync(new ForecastChart(forecastRoot));

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


    }

}
