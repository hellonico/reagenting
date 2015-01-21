(ns garden.myfirst
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px em]]))

(def light-blue "#5B83AC")

(defstyles screen
  [
  [:body
   {:font-family "sans-serif"
    :font-size (px 16)
    :line-height 1.5}
    
    [:div {
  	:font-family "Verdana"
  	:margin-left "10%"
  	:margin-right "10%"
	}]]
   
   [:h1 {
   	:font-family "Poiret One"
  	:color light-blue
  	:font-size (px 42)
   	;:font-weight "bold"
  	:margin-top (px 30)
  	:margin-left "5%"
   }]

   [:blockquote {
	:background "#f9f9f9"
	:border-left "10px solid #ccc"
	:margin "2.0em 10px"
  :font-family "Poiret One"
  :font-size (em 1)
	:padding "0.5em 10px"
	:quotes ["\201C""\201D""\2018""\2019"]
	}
	[:before {
  		:color "#ccc"
  		:content "open-quote"
  		:font-size (em 4)
  		:line-height "0.1em"
  		:margin-right "0.25em"
  		:vertical-align "-0.4em"
  	}]
  	[:p {
  	:display "inline"
  	}]]

	[:#draggable { 
		:width (px 100)
		:height (px 100)
		:padding "0.5em"
		:margin "10px 10px 10px 0"
		}]
	[:#droppable 
	{ :width "150px"
	  :height "150px"
	  :padding "0.5em"
	  :margin "10px"
	  }]

  	[:#chartdiv {
  		:margin-left "-30%"
		:width "670px"
		:height "100px"
  		}]])