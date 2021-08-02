@echo off

if not exist results mkdir results
set REM_IP="192.168.1.36"
set NUM_THREADS=200
set NUM_SECONDS=100
set SERVICE_NAME=micronaut_native

apache-jmeter-5.4.1/bin/jmeter -Jjmeter.save.saveservice.output_format=csv -Jjmeter.save.saveservice.assertion_results_failure_message=false -Jjmeter.save.saveservice.assertion_results=none -Jjmeter.save.saveservice.data_type=false -Jjmeter.save.saveservice.label=true -Jjmeter.save.saveservice.response_code=false -Jjmeter.save.saveservice.response_data=false -Jjmeter.save.saveservice.response_data.on_error=false -Jjmeter.save.saveservice.response_message=false -Jjmeter.save.saveservice.successful=true -Jjmeter.save.saveservice.thread_name=true -Jjmeter.save.saveservice.time=true -Jjmeter.save.saveservice.subresults=false -Jjmeter.save.saveservice.assertions=false -Jjmeter.save.saveservice.latency=true -Jjmeter.save.saveservice.connect_time=false -Jjmeter.save.saveservice.samplerData=false -Jjmeter.save.saveservice.responseHeaders=false -Jjmeter.save.saveservice.requestHeaders=false -Jjmeter.save.saveservice.encoding=false -Jjmeter.save.saveservice.bytes=false -Jjmeter.save.saveservice.sent_bytes=false -Jjmeter.save.saveservice.url=false -Jjmeter.save.saveservice.filename=false -Jjmeter.save.saveservice.hostname=false -Jjmeter.save.saveservice.thread_counts=false  -Jjmeter.save.saveservice.sample_count=false -Jjmeter.save.saveservice.idle_time=false -Jjmeter.save.saveservice.default_delimiter=, -Jjmeter.save.saveservice.print_field_names=true -Jjmeter.save.saveservice.timestamp_format=ms -Jrem_ip=%REM_IP% -Jnum_threads=%NUM_THREADS% -Jramp_sec=%NUM_SECONDS% -n -t rsa_brute.jmx -l results/%SERVICE_NAME%_rsa_brute.csv

pause