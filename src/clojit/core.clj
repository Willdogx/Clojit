(ns clojit.core
  (:require [clojit.views.status :refer [status-view]])
  (:require [clojit.views.menu :refer [menu-bar]])
  (:require [clojit.views.custom-command :refer [execute-command-view]])
  (:require [clojure.java.io :refer [file]])
  (:require clojure.edn)
  (:import [javax.swing JFrame UIManager SwingUtilities])
  (:import [net.miginfocom.swing MigLayout]))

;; config
(def config-filename "config.edn")

(def config (atom (if (.exists (file config-filename))
                    (read-string (slurp config-filename))
                    {:repository-path nil})))

(defn update-config [& {:as opts}]
  (swap! config #(merge % opts))
  (spit "config.edn" (pr-str @config)))

;; swing
(defn set-native-look-and-feel []
  (UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName)))

;; swing components
(set-native-look-and-feel)

(def frame (doto (JFrame. "Clojit")))
(def pane (.getContentPane frame))

(defn update-frame-content
  ([]
   (fn [view]
     (.removeAll pane)
     (case view
       status (status-view pane (:repository-path @config) update-frame-content)
       execute-command (execute-command-view pane))
     (doto frame
       (.setContentPane pane)
       (.pack)
       (.setVisible true))))
  ([view]
   ((update-frame-content) view)))

(defn -main [& args]
  (SwingUtilities/invokeLater
   (fn []
     (doto frame
       (.setJMenuBar (menu-bar frame update-frame-content update-config))
       (.setSize 800 600)
       (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
       (.setLocationRelativeTo nil)
       (.pack))
     (.setLayout pane (MigLayout. "" "[grow]"))
     (update-frame-content 'status))))
