function Connection(settings) {
    var that = this;
    this.queue = [];
    this.currentRequest = null;
    this.lastRequestTime = null;
    
    var options = {
        getStateInterval: 3000,
        queueInterval: 500,
        timeout: 10000,
        test: false
    };
    $.extend(options, settings);
    
    function RemoteCall(url, method, data, isPoll) {
        this.url = url;
        this.method = method;
        this.data = data;
        this.isPoll = isPoll != null ? isPoll : false;
    }
    
    this.API = {
        getState: function() {
            var url = "/api/user/" + options.userId;
            that.enqueue(new RemoteCall(url, "GET", {}, false));
        },
        setMood: function(mood) {
            var url = "/api/user/" + options.userId + "/mood/" + mood;
            that.enqueue(new RemoteCall(url, "POST"));
        },
        
        startPoll: function() {
            that.API.getState();
            setInterval(that.API.getState, options.getStateInterval);
        }
    };
    
    this.makeRequest = function(remoteCall) {
        this.currentRequest = $.ajax({
            url: remoteCall.url,
            type: remoteCall.method,
            data: remoteCall.data,
            username: options.username,
            password: options.password,
            dataType: 'json',
            cache: false,
            timeout: options.timeout,
            success: function(response) { that.handleSuccess(response); },
            complete: function() { that.handleComplete(); },
            error: function(req, status, err) { that.handleError(req, status, err); }
        });
    }
    
    this.handleSuccess = function(response) {
        // If the request was a poll, and there is another request waiting to
        // go out that is not a poll, ignore this request (because the state
        // is about to be changed by the next request anyway)
        if(this.currentRequest.isPoll && this.queue.length > 0 && !this.queue[0].isPoll) {
            return;
        }
        
        if(options.success) {
            options.success(response);
        }
    }
    
    this.handleError = function(request, textStatus, errorThrown) {
        if(textStatus == "timeout") {
            return;
        }
        
        if(options.error) {
            options.error(request, textStatus, errorThrown);
        }
    };
    
    this.enqueue = function(remoteCall) {
        // If there is a poll in the queue, remove it, and add it to the end
        function getPoll() {
            for(var i = 0; i < that.queue.length; i++) {
                var item = that.queue[i];
                if(item.isPoll) {
                    that.queue.splice(i, 1);
                    return item;
                }
            }
            return null;
        }
        var poll = getPoll();
        
        that.queue.push(remoteCall);
        
        if(poll !== null && !remoteCall.isPoll) {
            that.queue.push(poll);
        }
        
        that.processQueue();
    };
    
    // Gets called when the request completes, whether it's successful, an
    // error or a timeout
    this.handleComplete = function() {
        this.currentRequest = null;
        this.processQueue();
    };
    
    
    var processing = false;
    this.processQueue = function() {
        //console.log('pr: ' + processing + ' ql: ' + that.queue.length);
        if(processing || options.test) {
            return;
        }
        processing = true;
        
        // Wait for pending requests to complete and for the queue to have
        // pending requests
        if(that.currentRequest != null || that.queue.length == 0) {
            processing = false;
            setTimeout(that.processQueue, options.queueInterval);
            return;
        }
        
        var remoteCall = that.queue.shift();
        this.lastRequestTime = new Date().getTime();
        this.makeRequest(remoteCall);
        processing = false;
        
        setTimeout(that.processQueue, options.queueInterval);
    };
    
    
    return this.API;
    
    
    /*
    this.testEnqueue = function() {
        var c = new Connection({test: true});
        
        c.enqueue(new RemoteCall("norm", "GET", {}));
        c.enqueue(new RemoteCall("norm", "GET", {}));
        c.enqueue(new RemoteCall("poll", "GET", {}, true));
        c.enqueue(new RemoteCall("norm", "GET", {}));
        c.enqueue(new RemoteCall("poll", "GET", {}, true));
        c.enqueue(new RemoteCall("poll", "GET", {}, true));
        c.enqueue(new RemoteCall("norm", "GET", {}));
        c.enqueue(new RemoteCall("norm", "GET", {}));
        c.enqueue(new RemoteCall("poll", "GET", {}, true));
        c.enqueue(new RemoteCall("poll", "GET", {}, true));
        
        console.log("c.queue.length == 6", c.queue.length == 6);
        console.log(c.queue.length);
    };
    */
}
