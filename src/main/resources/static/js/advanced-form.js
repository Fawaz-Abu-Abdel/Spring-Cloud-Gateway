// Advanced route form JavaScript functionality

// Initialize predicate and filter indices
let predicateIndex = 0;
let filterIndex = 0;

// Add a new predicate to the form
function addPredicate() {
    const container = document.getElementById('predicates-container');
    const div = document.createElement('div');
    div.className = 'dynamic-item';
    div.innerHTML = `
        <div class="dynamic-item-header">
            <span class="item-number">Predicate ${predicateIndex + 1}</span>
            <button type="button" class="btn btn-danger btn-small" onclick="this.parentElement.parentElement.remove()">Remove</button>
        </div>
        <div class="form-group">
            <label>Predicate Name</label>
            <input type="text" name="predicates[${predicateIndex}].name" placeholder="e.g., Path, Method, Header" />
            <div class="help-text">Examples: Path, Method, Header, Query, Host, After, Before</div>
        </div>
        <div class="form-group">
            <label>Arguments (key=value, one per line)</label>
            <textarea name="predicates[${predicateIndex}].args" placeholder="e.g., _genkey_0=/api/users/**" style="min-height: 60px;"></textarea>
            <div class="help-text">Format: key=value (one per line). For Path use: _genkey_0=/path/**</div>
        </div>
    `;
    container.appendChild(div);
    predicateIndex++;
}

// Add a new filter to the form
function addFilter() {
    const container = document.getElementById('filters-container');
    const div = document.createElement('div');
    div.className = 'dynamic-item';
    div.innerHTML = `
        <div class="dynamic-item-header">
            <span class="item-number">Filter ${filterIndex + 1}</span>
            <button type="button" class="btn btn-danger btn-small" onclick="this.parentElement.parentElement.remove()">Remove</button>
        </div>
        <div class="form-group">
            <label>Filter Name</label>
            <input type="text" name="filters[${filterIndex}].name" placeholder="e.g., StripPrefix, AddRequestHeader" />
            <div class="help-text">Examples: StripPrefix, AddRequestHeader, RewritePath, CircuitBreaker</div>
        </div>
        <div class="form-group">
            <label>Arguments (key=value, one per line)</label>
            <textarea name="filters[${filterIndex}].args" placeholder="e.g., _genkey_0=1" style="min-height: 60px;"></textarea>
            <div class="help-text">Format: key=value (one per line). For StripPrefix use: _genkey_0=1</div>
        </div>
    `;
    container.appendChild(div);
    filterIndex++;
}
