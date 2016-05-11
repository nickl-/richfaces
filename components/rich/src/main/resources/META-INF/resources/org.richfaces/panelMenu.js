/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


(function ($, rf) {

    rf.ui = rf.ui || {};

    var __DEFAULT_OPTIONS = {
        expandSingle : true,
        bubbleSelection : true
    };

    rf.ui.PanelMenu = rf.BaseComponent.extendClass({
            // class name
            name:"PanelMenu",

            /**
             * @class PanelMenu
             * @name PanelMenu
             *
             * @constructor
             * @param {String} componentId - component id
             * @param {Hash} options - params
             * */
            init : function (componentId, options) {
                $super.constructor.call(this, componentId);
                this.items = {};
                this.attachToDom();

                this.options = $.extend(this.options, __DEFAULT_OPTIONS, options || {});
                this.activeItem = this.__getValueInput().value;
                this.nestingLevel = 0;

                this.__addUserEventHandler("collapse");
                this.__addUserEventHandler("expand");
            },

            addItem: function(item) {
                this.items[item.itemName] = item;
            },

            deleteItem: function(item) {
                delete this.items[item.itemName];
            },

            getSelectedItem: function() {
                return this.getItem(this.selectedItem());
            },

            getItem: function (name) {
                var item = this.items[name];

                if (!item) { // name not found, try id
                    var menuItem = rf.component(name);
                    if (menuItem) {
                        item = this.items[menuItem.itemName];
                    }
                }
                return item;
            },

            /***************************** Public Methods  ****************************************************************/
            /**
             * @methodOf
             * @name PanelMenu#selectItem
             *
             * TODO ...
             *
             * @param {String} name
             * @return {void} TODO ...
             */
            selectItem: function (name) {
                var item = this.getItem(name);

                if (item) {
                    item.select();
                }
            },

            /**
             * @methodOf
             * @name PanelMenu#selectedItem
             *
             * TODO ...
             *
             * @return {String} TODO ...
             */
            selectedItem: function (id) {
                if (typeof id != "undefined") {
                    var valueInput = this.__getValueInput();
                    var prevActiveItem = valueInput.value;

                    this.activeItem = id;
                    valueInput.value = id;

                    for (var itemName in this.items) {
                        if (this.items.hasOwnProperty(itemName)) {
                            var item = this.items[itemName];
                            if (item.__isSelected()) {
                                item.__unselect();
                            }
                        }
                    }

                    return prevActiveItem;
                } else {
                    return this.activeItem;
                }
            },

            __getValueInput : function() {
                return document.getElementById(this.id + "-value");
            },

            /**
             * @methodOf
             * @name PanelMenu#expandAll
             *
             * expands all groups and subgroups
             */
            expandAll: function () {
                for (var item in this.items) {
                    if (this.items.hasOwnProperty(item)) {
                        if (this.items[item].expand) {
                            this.items[item].expand();
                        }
                    }
                }
            },

            /**
             * @methodOf
             * @name PanelMenu#collapseAll
             *
             * collapses all groups and subgroups
             */
            collapseAll: function () {
                for (var item in this.items) {
                    if (this.items.hasOwnProperty(item)) {
                        if (this.items[item].collapse) {
                            this.items[item].collapse();
                        }
                    }
                }
            },

            /**
             * @methodOf
             * @name PanelMenu#expandGroup
             *
             * expands a group with the given name
             *
             * @param {String} groupName - name of the group or id of the group component
             */
            expandGroup: function (groupName) {
                var group = this.getItem(groupName);

                if (group && group.expand) {
                    group.expand();
                }
            },

            /**
             * @methodOf
             * @name PanelMenu#collapseGroup
             *
             * collapses a group with the given name
             *
             * @param {String} groupName - name of the group or id of the group component
             */
            collapseGroup: function (groupName) {
                var group = this.getItem(groupName);

                if (group && group.collapse) {
                    group.collapse();
                }
            },


            /***************************** Private Methods ****************************************************************/


            __panelMenu  : function () {
                return $(rf.getDomElement(this.id));
            },

            __childGroups : function () {
                return this.__panelMenu().children(".rf-pm-top-gr")
            },

            /**
             * @private
             * */
            __addUserEventHandler : function (name) {
                var handler = this.options["on" + name];
                if (handler) {
                    //TODO nick - this will cause slowdowns in IE
                    rf.Event.bindById(this.id, name, handler);
                }
            },

            __isActiveItem: function(item) {
                return item.itemName == this.activeItem;
            },

            __collapseGroups : function (source) {
                var topGroup = source.__rfTopGroup();
                this.__childGroups().each(function (index, group) {
                    if (group.id != source.getEventElement() && (!topGroup || group.id != topGroup.id)) {
                        rf.component(group).__collapse();
                    }
                });

            },

            destroy: function () {
                rf.Event.unbindById(this.id, "." + this.namespace);
                $super.destroy.call(this);
            }
        });

    // define super class link
    var $super = rf.ui.PanelMenu.$super;

})(RichFaces.jQuery, RichFaces);
