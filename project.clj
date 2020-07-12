(defproject clojit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojars.nakkaya/miglayout "3.7.3.1"]]
  :main clojit.core
  :repl-options {:init-ns clojit.core}
  :plugins [[lein-cljfmt "0.6.8"]])