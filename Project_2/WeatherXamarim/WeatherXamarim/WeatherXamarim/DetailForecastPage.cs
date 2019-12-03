﻿using System;
using System.Diagnostics;
using WeatherApp.Models;
using Xamarin.Forms;

namespace WeatherXamarim
{
    internal class DetailForecastPage : ContentPage
    {
        private List info;
        private ToolbarItem _saveAddToolBarItem;


        public DetailForecastPage(List info)
        {

            this.info = info;
          
        /*    var layout = new StackLayout { Padding = new Thickness(5, 10) };
            this.Content = layout;*/

            Title = info.Date + " Forecast";
            _saveAddToolBarItem = new ToolbarItem()
            { Text = "Save" };
         
            /*ToolbarItems.Add(_saveAddToolBarItem);
            _saveAddToolBarItem.Clicked += _saveAddToolBarItem_Clicked;*/
            Content = new StackLayout
            {
                Children = {
                    new Label {
                        Text = "Todo list data goes here",
                        HorizontalOptions = LayoutOptions.Center,
                        VerticalOptions = LayoutOptions.CenterAndExpand
                    },
                     new Label { 
                         Text = info.weather[0].description,
                         HorizontalOptions = LayoutOptions.Center,
                        VerticalOptions = LayoutOptions.CenterAndExpand
                     },
                     new Label {
                         Text = info.main.humidity.ToString(),
                         HorizontalOptions = LayoutOptions.Center,
                        VerticalOptions = LayoutOptions.CenterAndExpand
                     },
                       new Label {
                         Text = info.clouds.all.ToString(),
                         HorizontalOptions = LayoutOptions.Center,
                        VerticalOptions = LayoutOptions.CenterAndExpand
                     },
                     new Label {
                         Text = info.wind.speed.ToString(),
                         HorizontalOptions = LayoutOptions.Center,
                        VerticalOptions = LayoutOptions.CenterAndExpand
                     },
                     new Label {
                         Text = info.wind.deg.ToString(),
                         HorizontalOptions = LayoutOptions.Center,  
                        VerticalOptions = LayoutOptions.CenterAndExpand
                     },

                }
            };
           /* if (info.rain != null)
                if (info.rain.__invalid_name__3h > 0)
                {
                    Content.
                }*/

        }

        private void _saveAddToolBarItem_Clicked(object sender, EventArgs e)
        {
            throw new NotImplementedException();
        }
    }
}