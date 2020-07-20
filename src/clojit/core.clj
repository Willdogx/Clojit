(ns clojit.core
  (:require [clojit.views.status :refer [status]])
  (:require [clojit.views.menu :refer [menu-bar repomenu]])
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
                    {:repositories []
                     :repository-path nil})))

(defn add-repository
  ([repository-path]
    (add-repository repository-path nil))
  ([repository-path active]
    ;; FIXME: when active is true change to nil all other repositories.
    (swap! config #(merge % {:repositories (conj (:repositories %) {:repository-path repository-path
                                                                    :active active})}))))

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
  (seq (:repository-path @config)))

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
  [frame menubar]
  (let [panel (JPanel.)
        view-handler (update-frame-content frame panel)]
    (.add menubar (repomenu view-handler))
    (.add frame (doto (JTabbedPane.)
                  (.addTab (last (s/split (:repository-path @config) #"/")) panel)))
    (view-handler 'status)))

(defn -main [& args]
  (SwingUtilities/invokeLater
    (fn []
      (let [frame (JFrame. "Clojit")
            pane  (.getContentPane frame)
            menubar (menu-bar frame (update-frame-content frame pane) update-config)]
        (doto frame
          ;; FIXME: menubar should be passed the panel inside the current active tab
          (.setSize 800 600)
          (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
          (.setLocationRelativeTo nil)
          (.setJMenuBar menubar))
        (if (repositories?)
          (show-tabs frame menubar)
          #_(update-frame-content frame pane 'status)
          (update-frame-content frame pane 'select-repo-message))))))
