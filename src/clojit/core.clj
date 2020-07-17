(ns clojit.core
  (:require [clojit.views.status :refer [status]])
  (:require [clojit.views.menu :refer [menu-bar]])
  (:require [clojit.views.custom-command :refer [execute-command-view]])
  (:require [clojit.views.commit :refer [commit]])
  (:require [clojit.views.diff :refer [diff]])
  (:require [clojure.java.io :refer [file]])
  (:require [clojure.string :as s])
  (:require clojure.edn)
  (:import [javax.swing JFrame UIManager SwingUtilities JLabel JTabbedPane JPanel]))

;; config
(def config-filename "config.edn")

(def config (atom (if (.exists (file config-filename))
                    (read-string (slurp config-filename))
                    {:repositories []})))

(defn update-config [& {:as opts}]
  (swap! config #(merge % opts))
  (spit "config.edn" (pr-str @config)))

;; events
(def on-render (atom []))
(swap! on-render conj (fn [] "foo"))

;; swing components
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

(defn select-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn repositories?
  []
  (empty? (:repositories @config)))

(defn call-events
  [events]
  (doseq [event events]
    (event)))

(defn update-frame-content
  ([frame panel]
   (fn [view & args]
     (.removeAll panel)
     (let [repo-path (:repository-path @config)
           view-handler (update-frame-content frame panel)]
       (case view
         status (status panel repo-path view-handler on-render)
         execute-command (execute-command-view panel repo-path)
         commit (commit panel view-handler repo-path)
         diff (apply (partial  diff panel repo-path view-handler) args)
         select-repo-message (select-repo-message panel)))
     (.repaint panel)
     (.setVisible frame true)
     (call-events @on-render)
     ;; pack or not?
     #_(.pack frame)))
  ([frame pane view]
   ((update-frame-content frame pane) view)))

(defn show-tabs
  [frame]
  (let [panel (JPanel.)]
    (.add frame (doto (JTabbedPane.)
                  (.addTab (last (s/split (:repository-path @config) #"/")) panel)))
    (.setJMenuBar frame (menu-bar frame (update-frame-content frame panel) update-config))
    (update-frame-content frame panel 'status)))

(defn -main [& args]
  (SwingUtilities/invokeLater
   (fn []
     (let [frame (JFrame. "Clojit")
           pane  (.getContentPane frame)]
       (doto frame
         ;; FIXME: menubar should be passed the panel inside the current active tab
         (.setSize 800 600)
         (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
         (.setLocationRelativeTo nil))
       (if (repositories?)
         (show-tabs frame)
         #_(update-frame-content frame pane 'status)
         (update-frame-content frame pane 'select-repo-message))))))
