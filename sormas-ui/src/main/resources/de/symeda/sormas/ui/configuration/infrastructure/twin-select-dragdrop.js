/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

/**
 * JavaScript connector for drag-and-drop functionality in twin select table
 */
(function() {
    'use strict';
    
    /**
     * Initialize drag-and-drop for a Vaadin 7 Table
     * @param {Element} tableElement - The table DOM element
     * @param {Function} onDropCallback - Callback function when drop occurs (sourceIndex, targetIndex)
     */
    function initTableDragDrop(tableElement, onDropCallback) {
        if (!tableElement) {
            return;
        }
        
        var tbody = tableElement.querySelector('.v-table-body');
        if (!tbody) {
            return;
        }
        
        var draggedRow = null;
        var draggedIndex = -1;
        var placeholder = null;
        
        // Make rows draggable
        function makeRowsDraggable() {
            var rows = tbody.querySelectorAll('.v-table-row, .v-table-row-odd');
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                row.setAttribute('draggable', 'true');
                row.style.cursor = 'move';
                
                // Remove existing listeners to avoid duplicates
                row.removeEventListener('dragstart', handleDragStart);
                row.removeEventListener('dragend', handleDragEnd);
                row.removeEventListener('dragover', handleDragOver);
                row.removeEventListener('drop', handleDrop);
                row.removeEventListener('dragenter', handleDragEnter);
                row.removeEventListener('dragleave', handleDragLeave);
                
                // Add event listeners
                row.addEventListener('dragstart', handleDragStart);
                row.addEventListener('dragend', handleDragEnd);
                row.addEventListener('dragover', handleDragOver);
                row.addEventListener('drop', handleDrop);
                row.addEventListener('dragenter', handleDragEnter);
                row.addEventListener('dragleave', handleDragLeave);
            }
        }
        
        function handleDragStart(e) {
            draggedRow = this;
            draggedIndex = Array.prototype.indexOf.call(tbody.querySelectorAll('.v-table-row, .v-table-row-odd'), this);
            this.classList.add('dragging');
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/html', this.innerHTML);
            
            // Create placeholder
            placeholder = document.createElement('tr');
            placeholder.className = 'drag-placeholder';
            placeholder.style.height = this.offsetHeight + 'px';
            placeholder.style.backgroundColor = '#f0f0f0';
            placeholder.style.border = '2px dashed #999';
            var td = document.createElement('td');
            td.colSpan = 100;
            placeholder.appendChild(td);
        }
        
        function handleDragEnd(e) {
            this.classList.remove('dragging');
            if (placeholder && placeholder.parentNode) {
                placeholder.parentNode.removeChild(placeholder);
            }
            draggedRow = null;
            draggedIndex = -1;
            placeholder = null;
            
            // Remove hover effects from all rows
            var rows = tbody.querySelectorAll('.v-table-row, .v-table-row-odd');
            for (var i = 0; i < rows.length; i++) {
                rows[i].classList.remove('drag-over');
            }
        }
        
        function handleDragOver(e) {
            if (e.preventDefault) {
                e.preventDefault();
            }
            e.dataTransfer.dropEffect = 'move';
            return false;
        }
        
        function handleDragEnter(e) {
            if (this !== draggedRow) {
                this.classList.add('drag-over');
            }
        }
        
        function handleDragLeave(e) {
            this.classList.remove('drag-over');
        }
        
        function handleDrop(e) {
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            
            if (draggedRow === null || draggedRow === this) {
                return false;
            }
            
            var targetIndex = Array.prototype.indexOf.call(tbody.querySelectorAll('.v-table-row, .v-table-row-odd'), this);
            
            if (draggedIndex !== -1 && targetIndex !== -1 && draggedIndex !== targetIndex) {
                // Call the callback to handle the reordering on the server side
                if (onDropCallback && typeof onDropCallback === 'function') {
                    onDropCallback(draggedIndex, targetIndex);
                }
            }
            
            this.classList.remove('drag-over');
            return false;
        }
        
        // Initialize on load
        makeRowsDraggable();
        
        // Re-initialize when table content changes (using MutationObserver)
        var observer = new MutationObserver(function(mutations) {
            makeRowsDraggable();
        });
        
        observer.observe(tbody, {
            childList: true,
            subtree: true
        });
    }
    
    // Export function to global scope
    window.initTwinSelectDragDrop = initTableDragDrop;
})();
