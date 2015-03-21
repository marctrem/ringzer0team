(ns teamring0c13.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [pandect.algo.sha512 :as p512])
  (:gen-class))


(defn -main
  [session-token]

  (let [base-url "http://ringzer0team.com/challenges/13/"
        response1 (client/get base-url
                              {:cookies {"PHPSESSID" {:value session-token}}})

        hash (-> (:body response1)
                 html/html-snippet
                 (html/select [:.message])
                 first
                 :content
                 (nth 2)
                 (subs 3)
                 p512/sha512)

        response2 (client/get (str base-url hash)
                              {:cookies {"PHPSESSID" {:value session-token}}})

        flag (-> (:body response2)
                 html/html-snippet
                 (html/select [:.alert-info])
                 first
                 :content
                 first
                 )]

    (println flag)))

