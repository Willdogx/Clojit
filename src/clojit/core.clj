(ns clojit.core
  (:require [clojit.git :as git])
  (:require [clojure.java.io :refer [file]])
  (:require clojure.edn)
  (:import [javax.swing JMenu JMenuBar JMenuItem JFrame UIManager JLabel JList JFileChooser
            BoxLayout JScrollPane Box JPopupMenu SwingUtilities])
  (:import [java.awt Component Font Color Dimension])
  (:import [java.awt.event MouseAdapter ActionListener]))

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
(def repo-chooser (doto (JFileChooser.)
                    (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)))

(set-native-look-and-feel)

(def frame (JFrame. "Clojit"))

(defn configure-files-list [jlist list-content popup-menu]
  (doto jlist
    (.setListData (into-array list-content))
    (.setAlignmentX Component/LEFT_ALIGNMENT)
    (.addMouseListener
     (proxy [MouseAdapter] []
       (mousePressed [e]
         (when (.isPopupTrigger e)
           (.show popup-menu (.getComponent e) (.getX e) (.getY e))))
       (mouseReleased [e]
                     (when (.isPopupTrigger e)
                       (.show popup-menu (.getComponent e) (.getX e) (.getY e))))))))

(declare update-frame-content)

(defn add-status-header [pane repo-path]
  (let [label (JLabel. (str "On branch: " (git/get-cur-branch repo-path)))]
    (.add pane (doto label
                 (.setFont (.deriveFont (.getFont label) Font/BOLD))
                 (.setAlignmentX Component/LEFT_ALIGNMENT)))))

(defn get-unstage-menuitem [list repo-path]
  (doto (JMenuItem. "Unstage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/unstage (.getSelectedValuesList list) repo-path)
                            (update-frame-content (.getContentPane frame)))))))

(defn get-stage-menuitem [list repo-path]
  (doto (JMenuItem. "Stage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/stage (.getSelectedValuesList list) repo-path)
                            (update-frame-content (.getContentPane frame)))))))

(defn get-status-values-by-type [type repo-path]
  (let [list (JList.)]
    (case type
      staged {:files (git/get-staged-files repo-path)
              :label (JLabel. "Staged:")
              :list list
              :popup-menu-item (get-unstage-menuitem list repo-path)}
      modified {:files (git/get-modified-files repo-path)
                :label (JLabel. "Modified:")
                :list list
                :popup-menu-item (get-stage-menuitem list repo-path)}
      untracked {:files (git/get-untracked-files repo-path)
                 :label (JLabel. "Untracked:")
                 :list list
                 :popup-menu-item (get-stage-menuitem list repo-path)})))

(defn add-files-pane [pane type repo-path]
  (let [{:keys [files label list popup-menu-item]} (get-status-values-by-type type repo-path)]
    (when files
      (let [popup-menu (JPopupMenu.)]
        (doto popup-menu
          (.add popup-menu-item))
        (configure-files-list list files popup-menu)
        (doto pane
          (.add (doto label
                  (.setFont (.deriveFont (.getFont label) Font/BOLD))
                  (.setAlignmentX Component/LEFT_ALIGNMENT)))
          (.add (doto (JScrollPane. list)
                  (.setAlignmentX Component/LEFT_ALIGNMENT))))))))

(defn add-status-body [pane repo-path]
  (doseq [type ['staged 'modified 'untracked]]
    (add-files-pane pane type repo-path)))

(defn make-git-status-component [pane]
  ((juxt add-status-header
         add-status-body) pane (:repository-path @config)))

(defn make-config-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository...")
               (.setAlignmentX Component/LEFT_ALIGNMENT))))

(defn update-frame-content [pane]
  (doto pane
    (.removeAll)
    (.setLayout (BoxLayout. pane BoxLayout/Y_AXIS))
    (.setBackground (Color/WHITE)))
  (apply
   (if (git/repository? (:repository-path @config))
     make-git-status-component
     make-config-repo-message)
   [pane])
  (.setContentPane frame pane)
  (.setVisible frame true))

(defn unstage-action-listener [])

(defn menu-item-open-repository []
  (doto (JMenuItem. "Open Repository...")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (when (= (.showOpenDialog repo-chooser frame) JFileChooser/APPROVE_OPTION)
                              ; Save the path of the repo in a config file and initialize git commands
                              (let [repo-path (-> repo-chooser (.getSelectedFile) (.getAbsolutePath))]
                                (if (git/repository? repo-path)
                                  (do (update-config :repository-path repo-path)
                                      (update-frame-content (.getContentPane frame)))
                                  (println "Directory " repo-path " is not a git repository!")))))))))

(defn menu-item-update-status []
  (doto (JMenuItem. "Update status")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (update-frame-content (.getContentPane frame)))))))

(defn menu-bar []
  (doto (JMenuBar.)
    (.add (doto (JMenu. "Menu")
            (.add (menu-item-open-repository))
            (.add (menu-item-update-status))))))

(defn -main [& args]
  (SwingUtilities/invokeLater
   #(doto frame
      (.setJMenuBar (menu-bar))
      (-> (.getContentPane) (update-frame-content))
      (.setSize 800 600)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))))
