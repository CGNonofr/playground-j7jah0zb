var PIXI = PIXI || top.PIXI;
var Proton = Proton || top.Proton;


/*
 * #########################################################
 * ####                                                 ####
 * ####       EDIT FROM HERE                            ####
 * ####                                                 ####
 * #########################################################
 */
var Drawer = function() {
  this.currentFrame=0;
};

Drawer.VERSION = 1;

/** Mandatory */
Drawer.prototype.getGameName = function() {
  return "TheGreatEscape";
};

Drawer.getGameRatio = function() {
  return 1920/1080;
};

/** Mandatory */
Drawer.prototype.getResources = function() {
  return {
//    baseUrl : 'http://localhost/images/great-escape/',
    baseUrl : 'http://files.codingame.com/codingame/great-escape/',
    images : {
      background: 'background.jpg',
    },
    sprites : [],
    fonts : [],
    others : []
  };
};

Drawer.prototype.getOptions = function() {
  var drawer = this;
  return [{
    get: function() {
      return drawer.minimalWay;
    },
    set: function(value) {
      drawer.minimalWay=value;
    },
    title: 'SHORTEST PATH',
    values : {'VISIBLE': true, 'HIDDEN': false}
  }];
};

Drawer.prototype.getBackground = function() {
  return 0x000000;
};

/** Mandatory */
Drawer.prototype.initPreload = function(scope, container, progress, canvasWidth, canvasHeight) {
  scope.canvasWidth = canvasWidth;
  scope.canvasHeight = canvasHeight;

  scope.loaderProgress = new PIXI.Text("100", {
    font : "900 " + (canvasHeight * 0.117) + "px Lato",
    fill : "white",
    align : "center"
  });

  scope.loaderProgress.anchor.y = 1;
  scope.loaderProgress.anchor.x = 1.3;
  scope.progress = scope.realProgress = progress;
  scope.loaderProgress.position.y = canvasHeight;

  scope.progressBar = new PIXI.Graphics();
  container.addChild(scope.progressBar);
  container.addChild(scope.loaderProgress);
};
/** Mandatory */
Drawer.prototype.preload = function(scope, container, progress, canvasWidth, canvasHeight, obj) {
  scope.progress = progress;
};

/** Mandatory */
Drawer.prototype.renderPreloadScene = function(scope, step) {
  var stepFactor = Math.pow(0.998, step);
  scope.realProgress = stepFactor * scope.realProgress + (1 - stepFactor) * scope.progress;
  scope.loaderProgress.setText((scope.realProgress * 100).toFixed(0));
  scope.loaderProgress.position.x = scope.realProgress * scope.canvasWidth;

  scope.progressBar.clear();

  scope.progressBar.beginFill(0x0, 1);
  scope.progressBar.drawRect(0, 0, scope.canvasWidth * scope.realProgress + 1, scope.canvasHeight);
  scope.progressBar.endFill();

  scope.progressBar.beginFill(0x3f4446, 1);
  scope.progressBar.drawRect(scope.canvasWidth * scope.realProgress, 0, scope.canvasWidth, scope.canvasHeight);
  scope.progressBar.endFill();
  return true;
};

/** Mandatory */
Drawer.prototype.initDefaultScene = function(scope, container, canvasWidth, canvasHeight, proton) {
};

/** Mandatory */
Drawer.prototype.renderDefaultScene = function(scope, step, proton) {

  return true;
};

Drawer.prototype.endDefaultScene = function(scope, step, proton) {
  return true;
};

/** Mandatory */
Drawer.prototype.parseInitData = function(question, viewLines, playerCount) {
  return {};
};

/** Mandatory */
Drawer.prototype.getInitLineCount = function(frame) {
  return 0;
};

/** Mandatory */
Drawer.prototype.parseFrame = function(frame, keyFrame, previousFrames, initData) {
	if(frame.length>0) {
		var url='data:image/jpg;base64,'+frame[0];
		var image = new Image();
		image.src = url;
		document.body.appendChild(image);
		new PIXI.ImageLoader(url).load();
		PIXI.Texture.addTextureToCache(PIXI.Texture.fromImage(url), 'line'+previousFrames.length);
	}
	return {};
};


/** Mandatory */
Drawer.prototype.initScene = function(initData, scope, question, container, canvasWidth, canvasHeight, frames, colors, playerMapper, proton) {
	var i;
	var drawer=this;
	for(i=0;i<frames.length;++i) {
		var sprite=PIXI.Sprite.fromFrame('line'+i);
		sprite.frame=i;
		Object.defineProperty(sprite, 'visible', {
			get: function() {
				return drawer.currentFrame>=this.frame;
			}
		});
		sprite.position.y=i;
		container.addChild(sprite);
	}
	
};




/** Mandatory */
Drawer.prototype.updateScene = function(scope, question, frames, frameNumber, progress, speed, reason, proton) {
  /** ************************************* */
  /*        SYNCHRONOUS                     */
  /** ************************************* */
  this.currentFrame=frameNumber;
  
  var frame=frames[frameNumber];
};




/** Mandatory */
Drawer.prototype.renderScene = function(scope, question, frames, frameNumber, progress, speed, reason, step, proton) {
  /** ************************************* */
  /*        ASYNCHRONOUS                    */
  /** ************************************* */
  var render = true;

  return render;
};


/*
 * #########################################################
 * ####                                                 ####
 * ####         EDIT TO HERE                            ####
 * ####                                                 ####
 * #########################################################
 */

Drawer.prototype.getCurrentState = function() {
  if (this.loaded >= 1) {
    if (this.currentFrame >= 0) {
      return 'game';
    } else {
      return 'startScreen';
    }
  } else {
    return 'loading';
  }
};

Drawer.prototype.enableAsyncRendering = function(enabled) {
  this.asyncRendering = enabled;
};

Drawer.prototype.destroy = function() {
  this.destroyed = true;
};

Drawer.prototype.purge = function() {
  if (this.proton) {
    this.proton.destory();
  }
  this.scope = [];
  if (this.container.children.length > 0) {
    this.container.removeChildren();
  }
  this.changed = true;
};

Drawer.prototype.reinitScene = function() {
  if (this.loaded >= 1) {
    this.purge();
    this.asyncRenderingTime = 2000;
    this.initScene(this.initData, this.scope, this.question, this.container, this.initWidth, this.initHeight, this.frames, this.colors, this.playerMapper, this.proton);
    this.updateScene(this.scope, this.question, this.frames, this.currentFrame, this.progress, this.speed, this.reasons[this.currentFrame], this.proton);
    this.changed = true;
  }
};

Drawer.prototype.reinitDefaultScene = function() {
  if (this.loaded >= 1) {
    this.intro = true;
    this.purge();
    this.asyncRenderingTime = 2000;
    this.initDefaultScene(this.scope, this.container, this.initWidth, this.initHeight, this.proton);
    this.changed = true;
  }
};

Drawer.prototype.reinitLoadingScene = function() {
  if (this.loaded < 1) {
    this.purge();
    this.asyncRenderingTime = 2000;
    this.initPreload(this.scope, this.container, this.loaded, this.initWidth, this.initHeight);
  }
};

Drawer.prototype.reinit = function(force) {
  if (this.loaded >= 1) {
    if (this.currentFrame >= 0 && !this.intro) {
      this.reinitScene();
    } else {
      if(!this.intro || force) this.reinitDefaultScene();
    }
  } else {
    this.reinitLoadingScene();
  }
};

Drawer.prototype.animate = function(time) {
  if (!this.lastRenderTime)
    this.lastRenderTime = time;
  var step = time - this.lastRenderTime;
  if (this.asynchronousStep) {
    step = this.asynchronousStep;
  }
  if (this.onBeforeRender) {
    this.onBeforeRender();
  }
  this.asyncRenderingTime -= step;

  if (this.loaded < 1) this.changed |= this.renderPreloadScene(this.scope, step);
  else if (this.changed || this.asyncRendering || this.asyncRenderingTime > 0) {
    if (this.currentFrame < 0) this.changed |= this.renderDefaultScene(this.scope, step, this.proton);
    else if (this.intro) {
      this.changed = true;
      if (this.endDefaultScene(this.scope, step, this.proton)) {
        this.intro = false;
        this.reinit(true);
      }
    } else this.changed |= this
        .renderScene(this.scope, this.question, this.frames, this.currentFrame, this.progress, this.speed, this.reasons[this.currentFrame], step, this.proton)
  }
  if (this.changed) {
    this.proton.update();
    this.renderer.render(this.stage);
    this.changed = false;
  } else {
    this.stage.interactionManager.update();
  }
  if (this.onAfterRender) {
    this.onAfterRender();
  }
  var self = this;
  this.lastRenderTime = time;
  if (!this.destroyed)
    requestAnimationFrame(this.animate.bind(this));
};

Drawer.prototype.handleInitFrame = function(frame) {
  this.question = frame[1];
  var header = frame[0].split(" ");
  this.currentFrame = header[1] | 0;
  this.progress = 1;
  if (header.length > 2)
    this.reasons[i] = header[2];
  frame = frame.slice(1);
  var startLine = this.getInitLineCount(frame);
  this.initView = frame.slice(0, startLine);
  
  this.initData = this.parseInitData(this.question, this.initView, this.playerCount);
  return frame.slice(startLine, -1);
};

Drawer.prototype._initFrames = function(playerCount, frames) {
  var firstFrame = frames[0];
  if (firstFrame[0] == '-1') {
    this.currentFrame = -1;
    return;
  }
  this._frames = frames;
  this.playerCount = playerCount;
  this.reasons = [];
  this.frames = [];
  this.frames.push(this.parseFrame(this.handleInitFrame(firstFrame), true, this.frames, this.initData));
  for (var i = 1; i < this._frames.length; ++i) {
    var temp = this._frames[i];
    var header = temp[0].split(" ");
    if (header.length > 2) {
      this.reasons[i] = header[2];
    }
    this.frames.push(this.parseFrame(temp.slice(1, -1), header[0] == 'KEY_FRAME', this.frames, this.initData));
  }
};

Drawer.prototype.initFrames = function(playerCount, frames, playerMapper) {
  if (playerMapper)
    this.playerMapper = playerMapper;
  this._initFrames(playerCount, frames);
  this.reinit(false);
};

Drawer.prototype.update = function(currentFrame, progress, speed) {
  if (this.currentFrame >= 0) {
    this.asyncRenderingTime = 2000;
    this.changed = true;
    this.speed = speed * 2;
    this.currentFrame = currentFrame;
    this.progress = progress;
    if (this.loaded >= 1 && !this.intro) {
      this.updateScene(this.scope, this.question, this.frames, currentFrame, progress, this.speed, this.reasons[this.currentFrame], this.proton);
    }
  }
};

Drawer.prototype.parseColor = function(color) {
  if(Array.isArray(color)) {
    var i;
    var parsedColor=[];
    for(i=0;i<color.length;++i) {
      parsedColor.push(this.parseColor(color[i]));
    }
    return parsedColor;
  } else {
    return parseInt(color.substring(1),16);
  }
};

/** compatibilité ancien moteur * */
Drawer.prototype.draw = function(view, time, width, height, colors, progress, views, speed) {
  var header = view.split('\n')[0].split(" ");
  var frameNumber = header[1] | 0;
  this.colors = this.parseColor(colors);
  if (frameNumber <= 0) {
    var _views = []
    for (var i = 0; i < views.length; ++i) {
      _views.push(views[i].split("\n"));
    }
    this._initFrames(1, _views);
    if (view != this.lastView) {
      this.reinit(true);
      this.lastView = view;
    }
  }
  if (frameNumber >= 0)
    this.update(frameNumber, progress, speed);
};

Drawer.prototype.init = function(canvas, width, height, colors, playerNames, oversampling) {
  this.oversampling = oversampling || 1;
  this.canvas = canvas;
  if (!this.playerMapper)
    this.playerMapper = [0, 1, 2, 3, 4, 5, 6, 7];
  if (colors)
    this.colors = this.parseColor(colors);
  this.asyncRendering = true;
  this.asyncRenderingTime = 0;
  this.destroyed = false;
  this.asynchronousStep = undefined;
  var self = this;
  this.initWidth = width;
  this.initHeight = height;
  if (!this.alreadyLoaded) {
    this.alreadyLoaded = true;
    // Initialisation
    this.question = null;
    this.scope = null;
    this.stage = null;
    this.currentFrame = -1;
    this.loaded = 0;
    // Engine instanciation
    this.stage = new PIXI.Stage(this.getBackground());
    this.container = new PIXI.DisplayObjectContainer();
    this.stage.addChild(this.container);
    this.renderer = this.createRenderer(width, height, canvas);
    this.instantiateProton();
    var assetsToLoader = [];
    var resources = this.getResources();

    for ( var key in resources.images) {
      assetsToLoader.push(resources.baseUrl + resources.images[key]);
    }
    var i;
    for (i = 0; i < resources.sprites.length; ++i)
      assetsToLoader.push(resources.baseUrl + resources.sprites[i]);
    for (i = 0; i < resources.fonts.length; ++i)
      assetsToLoader.push(resources.baseUrl + resources.fonts[i]);
    for ( var key in resources.spines) {
      assetsToLoader.push(resources.baseUrl + resources.spines[key]);
    }
    for (i = 0; i < resources.others.length; ++i)
      assetsToLoader.push(resources.baseUrl + resources.others[i]);

    loader = new PIXI.AssetLoader(assetsToLoader, "Anonymous");
    var assetCounter = 0;
    self.scope = [];
    self.initPreload(self.scope, self.container, self.loaded = 0, self.initWidth, self.initHeight);
    requestAnimationFrame(self.animate.bind(self));
    loader.onProgress = function(obj) {
      self.preload(self.scope, self.container, self.loaded = ++assetCounter / assetsToLoader.length, self.initWidth, self.initHeight, obj);
    }

    loader.onComplete = function() {
      for ( var key in resources.images) {
        PIXI.Texture.addTextureToCache(PIXI.Texture.fromImage(resources.baseUrl + resources.images[key]), key)
      }
      for ( var key in resources.spines) {
        PIXI.AnimCache[key] = PIXI.AnimCache[resources.baseUrl + resources.spines[key]];
      }
      self.loaded = 1;
      self.reinit(true);
      self.changed = true;
    }
    if(assetsToLoader.length<=0) {
      loader.onComplete();
    }
    loader.onError = function(e) {
      console.log(e);
    }
    loader.load();
  } else {
    this.changed = true;
    this.renderer.resize(width, height);
    this.reinit(true);
  }
};
Drawer.prototype.resize = function() {

};
Drawer.prototype.createRenderer = function(width, height, canvas) {
  var renderer;
  var firefox = top.navigator.userAgent.indexOf("Firefox") >= 0 || top.navigator.userAgent.indexOf("Trident") >= 0;
  if (firefox) {
    renderer = new PIXI.CanvasRenderer(width, height, canvas, false, true, true);
  } else {
    renderer = PIXI.autoDetectRecommendedRenderer(width, height, canvas, false, true, true);
  }
  return renderer;
};

Drawer.prototype.instantiateProton = function() {
  this.proton = new Proton();
  var protonRenderer = new Proton.Renderer('other', this.proton);
  var container = this.container;

  protonRenderer.onProtonUpdate = function() {

  };
  protonRenderer.onParticleCreated = function(particle) {
    var particleSprite = new PIXI.Sprite(particle.target.texture);
    particle.sprite = particleSprite;
    particle.target.parent.addChild(particle.sprite);
  };

  protonRenderer.onParticleUpdate = function(particle) {
    particle.sprite.position = particle.p;
    particle.sprite.scale.x = particle.sprite.scale.y = particle.radius;
    particle.sprite.anchor.x = particle.sprite.anchor.y = 0.5;
    particle.sprite.alpha = particle.alpha;
    particle.sprite.rotation = particle.rotation * Math.PI / 180;
  }

  protonRenderer.onParticleDead = function(particle) {
    if (particle.target.parent.children.indexOf(particle.sprite) > 0) {
      particle.target.parent.removeChild(particle.sprite);
    }
  };

  protonRenderer.start();
};
