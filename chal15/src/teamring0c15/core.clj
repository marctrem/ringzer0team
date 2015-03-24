(ns teamring0c15.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clojure.java.shell :as shell]
            [clojure.java.io :as io])
  (:gen-class)
  (:import (java.util Base64)
           (org.apache.commons.lang3 ArrayUtils)))


(defn -main
  [session-token]

  (let [elf-mag0 (str (char 0x7f))

        temp-dir (-> (shell/sh "mktemp" "-d")
                     :out
                     clojure.string/trim)

        insomnia-lib (str temp-dir "/insomnia.so")

        _ (println "Working in" temp-dir)
        _ (println (-> (shell/sh "make"
                                 (str "DEST=\"" insomnia-lib "\""))
                       :out))

        base-url "http://ringzer0team.com/challenges/15/"
        response1 (client/get base-url
                              {:cookies {"PHPSESSID" {:value session-token}}})

        binary (-> (:body response1)
                   html/html-snippet
                   (html/select [:.message])
                   first
                   :content
                   (nth 2)
                   (subs 3)
                   .getBytes)

        binary (->
                 (loop [res binary]
                   (if (= (last res) 0x7f) ; 0x7f is the first byte of an ELF file and not a valid Base64 byte.
                     res
                     (recur (-> (Base64/getDecoder) (.decode res))))))


        _ (ArrayUtils/reverse binary) ; State :(

        bin-path (str temp-dir "/elfmsg")

        os (io/output-stream bin-path)

        _ (.write os binary)
        _ (.close os)

        _ (shell/sh "chmod" "+x" bin-path)

        bin-result (-> (shell/sh bin-path :env {"LD_PRELOAD" insomnia-lib})
                       :out)



        response2 (client/get (str base-url bin-result)
                              {:cookies {"PHPSESSID" {:value session-token}}})

        flag (-> (:body response2)
                 html/html-snippet
                 (html/select [:.alert-info])
                 first
                 :content
                 first)]

    (println flag)
    (System/exit 0)))

