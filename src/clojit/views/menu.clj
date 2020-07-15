(ns clojit.views.menu
  (:require [clojit.git :as git])
  (:import [javax.swing JFileChooser JMenuItem JMenuBar JMenu KeyStroke JOptionPane])
  (:import [java.awt.event ActionListener KeyEvent]))

(def repo-chooser (doto (JFileChooser.)
                    (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)))

(defn menuitem
  [name & {:keys [keys on-click]}]
  (let [item (JMenuItem. name)]
    (when keys
      ;; TODO: parse keys from string. E.g. "ALT-X" or "CTRL-O" or "C-o"
      (.setAccelerator item (apply #(KeyStroke/getKeyStroke %1 %2) keys)))
    (when on-click
      (.addActionListener item (reify ActionListener
                                 (actionPerformed [this _]
                                   (on-click)))))
    item))

(defn menubar
  [name & items]
  (let [menubar (JMenuBar.)
        menu (JMenu. name)]
    (doseq [{:keys [name keys on-click]} items]
      (.add menu (menuitem name :keys keys :on-click on-click)))
    (doto menubar (.add menu))))

(defn menu-bar [frame view-handler config-updater]
  (menubar "Menu"
    {:name "Open Repository..."
     :keys [KeyEvent/VK_O KeyEvent/CTRL_DOWN_MASK]
     :on-click (fn []
                       (when (= (.showOpenDialog repo-chooser frame) JFileChooser/APPROVE_OPTION)
                              ; Save the path of the repo in a config file and initialize git commands
                         (let [repo-path (-> repo-chooser (.getSelectedFile) (.getAbsolutePath))]
                           (if (git/repository? repo-path)
                             (do (config-updater :repository-path repo-path)
                               (view-handler 'status))
                             (JOptionPane/showMessageDialog
                               frame
                               (str "Directory " repo-path " is not a git repository!"))))))}
    {:name "Status"
     :keys [KeyEvent/VK_S KeyEvent/ALT_DOWN_MASK]
     :on-click #(view-handler 'status)}
    {:name "Execute Command"
     :keys [KeyEvent/VK_X KeyEvent/ALT_DOWN_MASK]
     :on-click #(view-handler 'execute-command)}
    {:name "Commit"
     :keys [KeyEvent/VK_C KeyEvent/ALT_DOWN_MASK]
     :on-click #(view-handler 'commit)}))