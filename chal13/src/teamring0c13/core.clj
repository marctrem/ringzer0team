(ns teamring0c13.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [pandect.algo.sha512 :as p512])
  (:gen-class))


(defn -main
  [session-token]

  (let [response1 (client/get "http://ringzer0team.com/challenges/13"
                              {:cookies {"PHPSESSID" {:value "5qk0lk5ovtbt89crs6lvvvhs15"}}})

        hash (-> (:body response1)
                 html/html-snippet
                 (html/select [:.message])
                 first
                 :content
                 (nth 2)
                 (subs 3)
                 p512/sha512)

        response2 (client/get (str "http://ringzer0team.com/challenges/13/" hash)
                              {:cookies {"PHPSESSID" {:value "5qk0lk5ovtbt89crs6lvvvhs15"}}})

        flag (-> (:body response2)
                 html/html-snippet
                 (html/select [:.alert-info])
                 first
                 :content
                 first
                 )
        ]
    (println flag)))

