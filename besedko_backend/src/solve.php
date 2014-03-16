<?php
/*
sudo apt-get install php-http
sudo apt-get install php-http-request
sudo apt-get install curl
sudo apt-get install php5-curl
sudo apt-cache search libcurl
sudo pecl install pecl_http

 * 
 * sudo apt-get install libcurl-ocaml-dev php-pear
 * 
 * You should add "extension=http.so" to php.ini

*/

// images search
// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=tree

//$num= $_GET['num'];

include_once("config.php");

function getWordList() {
	$st = file_get_contents("1-1000.txt");
	return explode("\n", $st);
}

function serve_request($links, $requested, $solutions) {
	$posts = array ("requested"=>$requested,
			"solutions" => $solutions,
			"images" => $links);
	return json_encode($posts);
}

function get_translations($get_slovenian, $direction) {
	//http://www.mkfoster.com/2009/01/04/how-to-install-a-php-pecl-extensionmodule-on-ubuntu/
	//http://glosbe.com/gapi_v0_1/translate?from=eng&dest=sl&format=json&phrase=tree&pretty=true
	$r = new HttpRequest('http://glosbe.com/gapi_v0_1/translate', HttpRequest::METH_GET);
	$r->setOptions(
		$options = array(
                        'useragent'      => "Firefox (+http://www.firefox.org)", // who am i 
                        'connecttimeout' => 120, // timeout on connect 
                        'timeout'          => 120, // timeout on response 
                        'redirect'          => 10, // stop after 10 redirects
                        'referer'           => "http://www.google.com"
                ));

	if ($direction == "eng->slo") {
		$r->addQueryData(array('from' => "eng"));
		$r->addQueryData(array('dest' => "sl"));
	} else {
		$r->addQueryData(array('from' => "sl"));
		$r->addQueryData(array('dest' => "eng"));
	}
	$r->addQueryData(array('format' => "json"));
	$r->addQueryData(array('phrase' => $get_slovenian));
	$r->addQueryData(array('pretty' => "true"));
	$r->send();
	//echo $r->getResponseCode();
	
	$deb = json_decode($r->getResponseBody());
	
	if (!isset($deb->tuc)) {
		return array();
	}

	$translations = array();
	for ($i=0; $i<count($deb->tuc); $i++) {
		if (isset($deb->tuc[$i]->phrase->text)) {
			$translations[count($translations)] = $deb->tuc[$i]->phrase->text;
		}
	}
	return array_values(array_unique($translations));
}

function get_images($keyword) {
	global $_KEY, $_CX;
//https://www.googleapis.com/customsearch/v1?key=AIzaSyDiXlEGw7Wn42fTS-HX8AGf1Zjz8blL_Wo&cx=017101698087921934063:7gj2nqmk3ny&fileType=jpg&searchType=image&q=tree
	$r = new HttpRequest('https://www.googleapis.com/customsearch/v1', HttpRequest::METH_GET);
	$r->addQueryData(array('key' => $_KEY));
	$r->addQueryData(array('cx' => $_CX));
	$r->addQueryData(array('fileType' => "jpg"));
	$r->addQueryData(array('searchType' => "image"));
	$r->addQueryData(array('q' => $keyword));
	$r->send();
	
	$response = $r->getResponseBody();
//print_r($response);
	$deb = json_decode($response);
	
	if (!isset($deb->items)) {
		return array();
	}
	
	$links=array();
	$items = $deb->items;
	for($i=0;$i<count($items);$i++) {
		$links[$i] = $items[$i]->link;
	}
	return $links;
}

function request() {
	$posts = array ("request"=>"trening",
			"phone_id" => 245);
	return json_encode($posts);
}

//request();

//function has_translations($word) { // english word
//	$good_translations = array();
//	$solutions = get_translations($word,"eng->slo");
//	
//	for ($i=0;$i<min(4,count($solutions));$i++) {
//		$solutions2 = get_translations($solutions[$i],"slo->eng");
//		if (in_array($word, $solutions2)) {
//			$good_translations[count($good_translations)] = $solutions[$i];
//		}
//	}
//	return $good_translations;
//}

//print_r( getWordList());
//$requested = "car";
$requested= $_GET['word'];

$slo_words = get_translations($requested, "eng->slo");
if (count($slo_words) > 0) {
	$english_words = get_translations($slo_words[0], "slo->eng");
	ob_start();
	//print_r($slo_words);
	//echo "\n";
	//print_r($english_words);
	
	$ins = ob_get_clean();

	//exec("echo '$ins' > /tmp/outp");
	 
	$links = get_images($requested);
	//$links = array("http://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Ash_Tree_-_geograph.org.uk_-_590710.jpg/220px-Ash_Tree_-_geograph.org.uk_-_590710.jpg"
		//,"http://newsroom.unl.edu/announce/files/file2887.jpg"
		//,"http://youaretheprimemover.com/wp-content/uploads/2012/09/tree-15_19_1-Tree-Sunrise-Northumberland_web.jpg");
	$ret = serve_request($links,$slo_words[0],$english_words);
} else {
	$ret = "null";
}

echo $ret;
//$good_trans = good_translations($requested);

//$links = array();
//print_r($solutions);


//print_r($good_trans);



?>





