(ns clojit.core
  (:require [clojit.views.status :refer [status]])
  (:require [clojit.views.menu :refer [menu-bar]])
  (:require [clojit.views.custom-command :refer [execute-command-view]])
  (:require [clojit.views.commit :refer [commit]])
  (:require [clojure.java.io :refer [file]])
  (:require clojure.edn)
  (:import [javax.swing JFrame UIManager SwingUtilities JLabel])
  (:import [net.miginfocom.swing MigLayout]))

;; config
(def config-filename "config.edn")

(def config (atom (if (.exists (file config-filename))
                    (read-string (slurp config-filename))
                    {:repositories []})))

(defn update-config [& {:as opts}]
  (swap! config #(merge % opts))
  (spit "config.edn" (pr-str @config)))

;; swing components
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

(defn select-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn repositories?
  []
  (empty? (:repositories @config)))

(defn update-frame-content
  ([frame pane]
   (fn [view]
     (.removeAll pane)
     (.setLayout pane (MigLayout. "" "[grow]"))
     (case view
       status (status pane (:repository-path @config) (update-frame-content frame pane))
       execute-command (execute-command-view pane (:repository-path @config))
       commit (commit pane (update-frame-content frame pane) (:repository-path @config))
       select-repo-message (select-repo-message pane))
     (doto frame
       (.setContentPane pane)
       (.setVisible true))))
  ([frame pane view]
   ((update-frame-content frame pane) view)))

(defn -main [& args]
  (SwingUtilities/invokeLater
   (fn []
     (let [frame (JFrame. "Clojit")
           pane  (.getContentPane frame)]
       (doto frame
         (.setJMenuBar (menu-bar frame (update-frame-content frame pane) update-config))
         (.setSize 800 600)
         (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
         (.setLocationRelativeTo nil))
       (if (repositories?)
         (update-frame-content frame pane 'status)
         (update-frame-content frame pane 'select-repo-message))))))
