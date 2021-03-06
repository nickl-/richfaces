DnD.SimpleDraggable  = Class.create();

Object.extend(DnD.SimpleDraggable.prototype, DnD.Draggable.prototype);
Object.extend(DnD.SimpleDraggable.prototype, {
	initialize: function(id, options) {
		this.id = $(id);
		
		if (!this.id) {
			alert("drag: Element with [" + id + "] ID was not found in the DOM tree. Probably element has no client ID or client ID hasn't been written. DnD's disabled. Check please!");
			return ;
		}
		
		this.options = options;

		this.dragIndicatorId = this.options.dragIndicator;

        this.eventMouseDown = this.initDrag.bindAsEventListener(this);
			
		Event.observe(this.id, "mousedown", this.eventMouseDown);
		
		this.form = this.id;
		while (this.form && !/^form$/i.test(this.form.tagName)) {
			this.form = this.form.parentNode;
		}
		
		this.enableDraggableCursors(this.options.grab, this.options.grabbing);
	},

	getDnDDragParams: function() {
		if (this.options.dndParams) {
			return this.options.dndParams.parseJSON(EventHandlersWalk);
		}
		
		return null;
	},

    getIndicator: function() {    	
    	var dragIndicator = $(this.dragIndicatorId);
        if (!dragIndicator) {
            dragIndicator = this.getOrCreateDefaultIndicator();
        }

        return dragIndicator;
    },

    ondragstart : function(event, drag) {
		drag.params = this.options.parameters;
		drag.params[this.id] = this.id;

		this.setIndicator(event);

		//this.dragEnter(event);
		
		if (this.form) {
			drag.params[this.form.id] = this.form.id;
		}
	},

	getContentType: function() {
		return this.options.dragType;
	},

	getDraggableOptions: function() {
		return this.options;
	},

	initDrag: function(event) {
		if (Event.isLeftClick(event)) {
		  var src = Event.element(event);
		  if(src.tagName && /^INPUT|SELECT|OPTION|BUTTON|TEXTAREA$/i.test(src.tagName)) 
				return;

			Event.stop(event);

			this.startDrag(event);
			//Event.observe(document, "mousemove", this.listenDragBound);
			//Event.observe(document, "mouseup", this.stopListenDragBound);
		}
	}
});
