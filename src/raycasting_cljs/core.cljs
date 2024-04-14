(ns raycasting-cljs.core
  (:require [com.stuartsierra.component :as component]))

(defrecord Entity [x y w h c]
  component/Lifecycle
  (start [this]
    (let [state (atom {:x x :y y
                       :w w :h h
                       :c c})
          move! (fn [x y]
                  (swap! state (fn [state] (-> state
                                               (update :x #(+ % x))
                                               (update :y #(+ % y))))))]
      (conj this
            {:state state
             :draw (fn [ctx]
                     (let [{x :x y :y w :w h :h c :c} @state]
                       (set! (.-fillStyle ctx) c)
                       (.fillRect ctx x y w h)))
             :move! move!
             :keyboard! (fn [key] (case key
                                    "ArrowRight" (move! 10 0)
                                    "ArrowLeft" (move! -10 0)
                                    "ArrowUp" (move! 0 -10)
                                    "ArrowDown" (move! 0 10)))}))))

(def entities (atom []))
(def pov (atom nil))

(defn draw-handler
  [gl]
  (.clearRect gl 0 0 640 480)
  ((:draw @pov) gl)
  (doall (map #((:draw %) gl) @entities)))

(defn keyboard-handler
  [event]
  (let [key (.-key event)]
    ; (js/console.log key)
    ((:keyboard! @pov) key)))

#_(set! (.-title js/document) "wasd")

(defn ^:export init
  []
  (let [root (.getElementById js/document "root")
        gl (.getContext root "2d")]
    (def root root)
    (def gl gl)
    (.addEventListener js/document "keydown" keyboard-handler)
    (reset! pov (component/start (->Entity 10 50 20 20 "red")))
    (reset! entities (map #(component/start (->Entity (* 50 %) (* 25 %) 30 30 "blue")) (take 5 (range))))
    (js/setInterval (fn [] (draw-handler gl)) 100)))

(js/console.log gl root)
