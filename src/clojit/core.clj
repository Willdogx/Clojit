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
(defn make-status-header [pane]
  (let [repo-path (:repository-path @config)
        staged-files (git/get-staged-files repo-path)
        untracked-files (git/get-untracked-files repo-path)
        modified-files (git/get-modified-files repo-path)
        v-margin (Box/createRigidArea (Dimension. 0 10))]
    (let [label (JLabel. (str "On branch: " (git/get-cur-branch repo-path)))]
      (.add pane (doto label
                   (.setFont (.deriveFont (.getFont label) Font/BOLD))
                   (.setAlignmentX Component/LEFT_ALIGNMENT))))
    (when staged-files
      (let [label (JLabel. "Staged:")
            popup-menu (JPopupMenu.)
            staged-files-list (JList.)]
        (doto popup-menu
          (.add (doto (JMenuItem. "Unstage")
                  (.addActionListener (proxy [ActionListener] []
                                        (actionPerformed [e]
                                          (git/unstage (.getSelectedValuesList staged-files-list) repo-path)
                                          (update-frame-content (.getContentPane frame))))))))
        (configure-files-list staged-files-list staged-files popup-menu)
        (doto pane
          (.add v-margin)
          (.add (doto label
                  (.setFont (.deriveFont (.getFont label) Font/BOLD))
                  (.setAlignmentX Component/LEFT_ALIGNMENT)))
          (.add v-margin)
          (.add (doto (JScrollPane. staged-files-list)
                  (.setAlignmentX Component/LEFT_ALIGNMENT))))))
    (when modified-files
      (let [label (JLabel. "Modified:")
            modified-files-list (JList.)
            popup-menu (JPopupMenu.)]
        (doto popup-menu
          (.add (doto (JMenuItem. "Stage")
                  (.addActionListener (proxy [ActionListener] [] 
                                        (actionPerformed [e]
                                          (git/stage (.getSelectedValuesList modified-files-list) repo-path)
                                          (update-frame-content (.getContentPane frame))))))))
        (configure-files-list modified-files-list modified-files popup-menu)
        (doto pane
          (.add v-margin)
          (.add (doto label
                  (.setFont (.deriveFont (.getFont label) Font/BOLD))
                  (.setAlignmentX Component/LEFT_ALIGNMENT)))
          (.add v-margin)
          (.add (doto (JScrollPane. modified-files-list)
                  (.setAlignmentX Component/LEFT_ALIGNMENT))))))
    (when untracked-files
      (let [label (JLabel. "Untracked:")
            untracked-files-list (JList.)
            popup-menu (JPopupMenu.)]
        (doto popup-menu
          (.add (doto (JMenuItem. "Stage")
                  (.addActionListener (proxy [ActionListener] []
                                        (actionPerformed [e]
                                          (git/stage (.getSelectedValuesList untracked-files-list) repo-path)
                                          (update-frame-content (.getContentPane frame))))))))
        (configure-files-list untracked-files-list untracked-files popup-menu)
        (doto pane
          (.add v-margin)
          (.add (doto label
                  (.setFont (.deriveFont (.getFont label) Font/BOLD))
                  (.setAlignmentX Component/LEFT_ALIGNMENT)))
          (.add v-margin)
          (.add (doto (JScrollPane. untracked-files-list)
                  (.setAlignmentX Component/LEFT_ALIGNMENT))))))))

(defn make-git-status-component [pane]
  (doto pane
    (make-status-header)))

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

(defn menu-bar []
  (doto (JMenuBar.)
    (.add (doto (JMenu. "Menu")
            (.add (menu-item-open-repository))))))

(defn -main [& args]
  (SwingUtilities/invokeLater
   #(doto frame
      (.setJMenuBar (menu-bar))
      (-> (.getContentPane) (update-frame-content))
      (.setSize 800 600)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))))
