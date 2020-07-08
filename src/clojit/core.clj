(ns clojit.core
  (:require [clojit.git :as git])
  (:require [clojure.java.io :refer [file]])
  (:require clojure.edn)
  (:import [javax.swing JMenu JMenuBar JMenuItem JFrame UIManager JLabel JList JFileChooser
            JScrollPane JPopupMenu SwingUtilities JTextField])
  (:import [java.awt Component Font Color BorderLayout])
  (:import [java.awt.event MouseAdapter ActionListener])
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
(def repo-chooser (doto (JFileChooser.)
                    (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)))

(set-native-look-and-feel)

(def frame (JFrame. "Clojit"))
(def pane (.getContentPane frame))

(defn configure-files-list [jlist list-content popup-menu]
  (doto jlist
    (.setListData (into-array list-content))
    (.setAlignmentX Component/LEFT_ALIGNMENT)
    #_(.setFixedCellHeight 1)
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
  (let [label (JLabel. (str "<html>Branch: <b>" (git/get-cur-branch repo-path) "</b> " (git/get-last-commit-message repo-path) "</html>"))]
    (.add pane (doto label
                 #_(.setFont (.deriveFont (.getFont label) Font/BOLD)))
          "wrap")))

(defn get-unstage-menuitem [list repo-path]
  (doto (JMenuItem. "Unstage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/unstage (.getSelectedValuesList list) repo-path)
                            (update-frame-content 'status))))))

(defn get-stage-menuitem [list repo-path]
  (doto (JMenuItem. "Stage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/stage (.getSelectedValuesList list) repo-path)
                            (update-frame-content 'status))))))

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
                  #_(.setFont (.deriveFont (.getFont label) Font/BOLD)))
                "wrap")
          (.add (doto (JScrollPane. list)
                  #_(.setPreferredSize (.getSize list)))
                "wrap"))))))

(defn add-status-body [pane repo-path]
  (doseq [type ['staged 'modified 'untracked]]
    (add-files-pane pane type repo-path)))

(defn make-git-status-component [pane]
  ((juxt add-status-header
         add-status-body) pane (:repository-path @config)))

(defn make-config-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository...")
               (.setAlignmentX Component/LEFT_ALIGNMENT))))

(defn status-view [pane]
  (apply
   (if (git/repository? (:repository-path @config))
     make-git-status-component
     make-config-repo-message)
   [pane]))

(defn execute-command-view [pane]
  (let [output-text (JTextField.)
        input (JTextField.)]
    ))

(defn update-frame-content [view]
  (.removeAll pane)
  ((case view
     status status-view
     execute-command execute-command-view) pane)
  (.setContentPane frame pane)
  (.setVisible frame true))

(defn menu-item-open-repository []
  (doto (JMenuItem. "Open Repository...")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (when (= (.showOpenDialog repo-chooser frame) JFileChooser/APPROVE_OPTION)
                              ; Save the path of the repo in a config file and initialize git commands
                              (let [repo-path (-> repo-chooser (.getSelectedFile) (.getAbsolutePath))]
                                (if (git/repository? repo-path)
                                  (do (update-config :repository-path repo-path)
                                      (update-frame-content 'status))
                                  (println "Directory " repo-path " is not a git repository!")))))))))

(defn menu-item-update-status []
  (doto (JMenuItem. "Update status")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (update-frame-content 'status))))))

(defn menu-item-execute-command []
  (doto (JMenuItem. "Execute command")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            )))))

(defn menu-bar []
  (doto (JMenuBar.)
    (.add (doto (JMenu. "Menu")
            (.add (menu-item-open-repository))
            (.add (menu-item-update-status))
            (.add (menu-item-execute-command))))))

(defn -main [& args]
  (SwingUtilities/invokeLater
   (fn []
     (doto frame
       (.setJMenuBar (menu-bar))
       (.setSize 800 600)
       (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
       (.setLocationRelativeTo nil))
     (doto pane
       (.setLayout (MigLayout.))
       #_(.setBackground (Color/WHITE)))
     (update-frame-content 'status))))
