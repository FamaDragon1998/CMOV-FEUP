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
        RootObjectForecast forecastRoot;
        private ToolbarItem _saveAddToolBarItem, _saveAddToolBarItem2;
        public List<String> AllCities;
        public List<String> FavoriteCities;

        public MainPage()
        {
            InitializeComponent();
            SetupCities();
   
            _saveAddToolBarItem2 = new ToolbarItem()
            { IconImageSource = "fav.png" };
            ToolbarItems.Add(_saveAddToolBarItem2);
            _saveAddToolBarItem2.Clicked += _saveAddToolBarItem_Clicked2;

            _saveAddToolBarItem = new ToolbarItem()
            { IconImageSource = "add.png" };
            ToolbarItems.Add(_saveAddToolBarItem);
            _saveAddToolBarItem.Clicked += _saveAddToolBarItem_Clicked;


            this.BindingContext = this;
            MakeRequest(FavoriteCities[0]);
        }

        public void MakeRequest(string city)
        {
            Title = city + ", Portugal";
            city = city.ToLower();
            GetServerInformation(city, "weather");
            GetServerInformation(city, "forecast");
        }

        private void OnPickerSelectedIndexChanged(object sender, EventArgs e)
        {
            Picker picker = addcity;
            var selectedItem = (String) picker.SelectedItem; // This is the model selected in the picker
            
            if (!FavoriteCities.Contains(selectedItem))
            {
                FavoriteCities.Add(selectedItem);
                favoritecities.Items.Add(selectedItem);
                
                OnPropertyChanged("favoritecities");

            }
        }

        private void OnPickerSelectedIndexChanged2(object sender, EventArgs e)
        {
            Picker picker = favoritecities;
            var selectedItem = (String)picker.SelectedItem; // This is the model selected in the picker
            MakeRequest(selectedItem);       
        }

        private void _saveAddToolBarItem_Clicked(object sender, System.EventArgs e)
        {
            addcity.Focus();
        }

        private void _saveAddToolBarItem_Clicked2(object sender, System.EventArgs e)
        {
            favoritecities.Focus();
        }

        public void GetServerInformation(string city, string type)
        {
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
            if (root.rain != null) {
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
            List info = (List)e.Item;
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
                case "thunderstorm":
                    WeatherBackground.Source = "zeushatesyou.png";
                    break;
                default:
                    if (description.Contains("rain"))
                    {
                        WeatherBackground.Source = "rainy.png";
                    }
                    else if (description.Contains("clouds"))
                    {
                        WeatherBackground.Source = "cloudy.png";
                    }
                    else if (description.Contains("mist"))
                    {
                        WeatherBackground.Source = "mist.png";
                    }
                    else if (description.Contains("snow"))
                    {
                        WeatherBackground.Source = "snow.png";
                    }
                    else WeatherBackground.Source = "overlay.png";
                    break;
            }
        
        }

        private void SetupCities()
        {
            FavoriteCities = new List<String>() {"Porto"};
            AllCities = new List<String>()
            {
               "Aveiro",
               "Beja",
               "Braga",
               "Bragança",
               "Castelo Branco",
               "Coimbra",
               "Évora",
               "Faro",
               "Guarda",
               "Leiria",
               "Lisboa",
               "Portalegre",
               "Porto",
               "Santarém",
               "Setúbal",
               "Viana do Castelo",
               "Vila Real",
               "Viseu"
            };
            addcity.ItemsSource = AllCities;
            favoritecities.Items.Add(FavoriteCities[0]);
        }
    }

    public class Districts
    {
        public string Name { get; set; }

        public Districts(string name)
        {
            Name = name;
        }
    }

}
